package org.cubecraftutilities.core.managers;

import org.cubecraftutilities.core.CCU;
import org.cubecraftutilities.core.managers.submanagers.DurabilityManager;
import org.cubecraftutilities.core.managers.submanagers.EggWarsMapInfoManager;
import org.cubecraftutilities.core.managers.submanagers.FriendTrackerManager;
import org.cubecraftutilities.core.managers.submanagers.PartyManager;
import org.cubecraftutilities.core.managers.submanagers.SpawnProtectionManager;

public class CCUManager {

  private final CCU addon;

  // Sub Managers

  private final PartyManager partyManager;
  private final EggWarsMapInfoManager eggWarsMapInfoManager;
  private final SpawnProtectionManager spawnProtectionManager;
  private final DurabilityManager durabilityManager;
  private final FriendTrackerManager friendTrackerManager;

  // Own fields

  private String serverIP;
  private String divisionName;
  private String lastDivisionName;
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


  public CCUManager(CCU addon) {
    this.addon = addon;

    this.partyManager = new PartyManager();
    this.eggWarsMapInfoManager = new EggWarsMapInfoManager(addon);
    this.spawnProtectionManager = new SpawnProtectionManager(addon);
    this.durabilityManager = new DurabilityManager();
    this.friendTrackerManager = new FriendTrackerManager();

    this.serverIP = "";
    this.divisionName = "";
    this.lastDivisionName = "";
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



  public PartyManager getPartyManager() {return this.partyManager;}
  public EggWarsMapInfoManager getEggWarsMapInfoManager() {return this.eggWarsMapInfoManager;}
  public DurabilityManager getDurabilityManager() {return this.durabilityManager;}
  public SpawnProtectionManager getSpawnProtectionManager() {return spawnProtectionManager;}
  public FriendTrackerManager getFriendTrackerManager() {return friendTrackerManager;}

  public void reset() {
    this.serverIP = "";
    this.lastDivisionName = "";
    this.divisionName = "";
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
  }

  public void onCubeJoin() {
    this.serverIP = "play.cubecraft.net";
    this.divisionName = "CubeCraft";
    this.lastDivisionName = this.divisionName;
    this.mapName = "Lobby";
    this.teamColour = "";

    this.eliminated = false;
    this.inPreLobby = true;

    this.chestPartyAnnounceCounter = 0;
    this.gameStartTime = -1;

    this.partyManager.reset();
  }

  public void onServerSwitch() {
    this.eliminated = false;
    this.inPreLobby = true;
    this.gameStartTime = -1;
    this.won = false;
    this.spawnProtectionManager.resetHasMap();
  }

  public void setTeamColour(String teamColour) {
    this.teamColour = teamColour;
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

  public String getDivisionName() {
    return divisionName;
  }

  public void setDivisionName(String divisionName) {
    this.lastDivisionName = this.divisionName;
    this.divisionName = divisionName;
  }

  public void setMapName(String mapName) {
    this.mapName = mapName;
  }

  public String getLastDivisionName() {
    return lastDivisionName;
  }

  public String getMapName() {
    return mapName;
  }

  public String getServerIP() {
    return serverIP;
  }

  public String getTeamColour() {
    return teamColour;
  }

  public String getBungeecord() {
    return bungeecord;
  }

  public String getServerID() {
    return serverID;
  }

  public void setBungeecord(String bungeecord) {
    this.bungeecord = bungeecord;
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
