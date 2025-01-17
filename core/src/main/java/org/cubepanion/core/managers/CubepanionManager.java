package org.cubepanion.core.managers;

import art.ameliah.libs.weave.ChestAPI.ChestLocation;
import art.ameliah.libs.weave.ChestAPI.SeasonType;
import art.ameliah.libs.weave.Result;
import art.ameliah.libs.weave.WeaveException;
import java.util.ArrayList;
import java.util.List;
import org.cubepanion.core.Cubepanion;
import org.cubepanion.core.managers.submanagers.DurabilityManager;
import org.cubepanion.core.managers.submanagers.EggWarsMapInfoManager;
import org.cubepanion.core.managers.submanagers.FriendTrackerManager;
import org.cubepanion.core.managers.submanagers.PartyManager;
import org.cubepanion.core.managers.submanagers.SpawnProtectionManager;
import org.cubepanion.core.utils.CubeGame;
import org.cubepanion.core.utils.LOGGER;

public class CubepanionManager {
  // Sub Managers

  private final PartyManager partyManager;
  private final EggWarsMapInfoManager eggWarsMapInfoManager;
  private final SpawnProtectionManager spawnProtectionManager;
  private final DurabilityManager durabilityManager;
  private final FriendTrackerManager friendTrackerManager;

  // Own fields

  private String serverIP;
  private CubeGame division;
  private CubeGame lastDivision;
  private String mapName;
  private String teamColour;
  private String bungeecord;
  private String serverID;

  private boolean eliminated;
  private boolean inPreLobby;
  private boolean won;
  private boolean hasUpdatedAfterServerSwitch;

  private boolean requestedFullFriendsList;

  private int chestPartyAnnounceCounter;

  private long gameStartTime;


  public CubepanionManager(Cubepanion addon) {
    this.partyManager = new PartyManager();
    this.eggWarsMapInfoManager = new EggWarsMapInfoManager(addon);
    this.spawnProtectionManager = new SpawnProtectionManager(addon);
    this.durabilityManager = new DurabilityManager();
    this.friendTrackerManager = new FriendTrackerManager();

    this.serverIP = "";
    this.division = CubeGame.NONE;
    this.lastDivision = CubeGame.NONE;
    this.mapName = "";
    this.teamColour = "";
    this.bungeecord = "";
    this.serverID = "";

    this.eliminated = false;
    this.inPreLobby = false;
    this.won = false;
    this.requestedFullFriendsList = false;
    this.hasUpdatedAfterServerSwitch = false;

    this.chestPartyAnnounceCounter = 0;
    this.gameStartTime = -1;
  }


  public PartyManager getPartyManager() {
    return this.partyManager;
  }

  public EggWarsMapInfoManager getEggWarsMapInfoManager() {
    return this.eggWarsMapInfoManager;
  }

  public DurabilityManager getDurabilityManager() {
    return this.durabilityManager;
  }

  public SpawnProtectionManager getSpawnProtectionManager() {
    return spawnProtectionManager;
  }

  public FriendTrackerManager getFriendTrackerManager() {
    return friendTrackerManager;
  }

  public void reset() {
    this.serverIP = "";
    this.lastDivision = CubeGame.NONE;
    this.division = CubeGame.NONE;
    this.teamColour = "";
    this.mapName = "";
    this.bungeecord = "";
    this.serverID = "";

    this.eliminated = false;
    this.inPreLobby = false;
    this.won = false;
    this.requestedFullFriendsList = false;
    this.hasUpdatedAfterServerSwitch = false;

    this.chestPartyAnnounceCounter = 0;
    this.gameStartTime = -1;

    this.partyManager.reset();
    this.durabilityManager.reset();
    this.friendTrackerManager.endCurrentLoop();
    this.friendTrackerManager.resetTrackers();

    Cubepanion.chestLocations = new ArrayList<>();
  }

  public void onCubeJoin() {
    this.serverIP = "play.cubecraft.net";
    this.division = CubeGame.LOBBY;
    this.lastDivision = this.division;
    this.mapName = "Lobby";
    this.teamColour = "";

    this.eliminated = false;
    this.inPreLobby = true;

    this.chestPartyAnnounceCounter = 0;
    this.gameStartTime = -1;

    this.partyManager.reset();
    this.eggWarsMapInfoManager.queryMaps();

    Result<ChestLocation[], WeaveException> locationResult = Cubepanion.weave.getChestAPI()
        .getCurrentChestLocations();
    if (locationResult.isErr()) {
      LOGGER.warn(getClass(), locationResult.getError(),
          "Could not update Cubepanion#chestLocations");
    } else {
      Cubepanion.chestLocations = List.of(locationResult.getValue());
    }

    Result<String[], WeaveException> seasonResult = Cubepanion.weave.getChestAPI()
        .getSeasons(SeasonType.RUNNING);
    if (seasonResult.isErr()) {
      LOGGER.warn(getClass(), seasonResult.getError(), "Could not update Cubepanion#season");
    } else {
      Cubepanion.season = seasonResult.getValue()[0];
    }
  }

  public void onServerSwitch() {
    this.eliminated = false;
    this.inPreLobby = true;
    this.gameStartTime = -1;
    this.won = false;
    this.spawnProtectionManager.resetHasMap();
  }

  public boolean onCubeCraft() {
    return this.serverIP.equals("play.cubecraft.net");
  }

  public boolean isEliminated() {
    return eliminated;
  }

  public void setEliminated(boolean eliminated) {
    this.eliminated = eliminated;
  }

  public boolean isInPreLobby() {
    return inPreLobby;
  }

  public void setInPreLobby(boolean inPreLobby) {
    this.inPreLobby = inPreLobby;
  }

  public boolean hasLost() {
    return !won;
  }

  public void setWon(boolean won) {
    this.won = won;
  }

  public boolean hasUpdatedAfterServerSwitch() {
    return hasUpdatedAfterServerSwitch;
  }

  public void setHasUpdatedAfterServerSwitch(boolean hasUpdatedAfterServerSwitch) {
    this.hasUpdatedAfterServerSwitch = hasUpdatedAfterServerSwitch;
  }

  public int getChestPartyAnnounceCounter() {
    return chestPartyAnnounceCounter;
  }

  public CubeGame getDivision() {
    return division;
  }

  public boolean isPlaying(CubeGame game) {
    return division.equals(game) && !inPreLobby;
  }

  public void setDivision(CubeGame division) {
    this.lastDivision = this.division;
    this.division = division;

    if (this.division.equals(CubeGame.SKYBLOCK)
        || this.division.equals(CubeGame.FFA)
        || CubeGame.isParkour(this.division)
        || this.division.equals(CubeGame.LOBBY)) {
      this.inPreLobby = false;
      this.gameStartTime = System.currentTimeMillis();
    }
  }

  public CubeGame getLastDivision() {
    return lastDivision;
  }

  public String getMapName() {
    return mapName;
  }

  public void setMapName(String mapName) {
    this.mapName = mapName;
  }

  public String getServerIP() {
    return serverIP;
  }

  public String getTeamColour() {
    return teamColour;
  }

  public void setTeamColour(String teamColour) {
    this.teamColour = teamColour;
  }

  public String getBungeecord() {
    return bungeecord;
  }

  public void setBungeecord(String bungeecord) {
    this.bungeecord = bungeecord;
  }

  public String getServerID() {
    return serverID;
  }

  public void setServerID(String serverID) {
    this.serverID = serverID;
  }

  public boolean hasRequestedFullFriendsList() {
    return requestedFullFriendsList;
  }

  public void setRequestedFullFriendsList(boolean requestedFullFriendsList) {
    this.requestedFullFriendsList = requestedFullFriendsList;
  }

  public long getGameStartTime() {
    return gameStartTime;
  }

  public void setGameStartTime(long gameStartTime) {
    this.gameStartTime = gameStartTime;
  }
}
