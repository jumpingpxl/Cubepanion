package org.cubepanion.v1_19_4;

import art.ameliah.libs.weave.ChestAPI.ChestLocation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Singleton;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import org.cubepanion.core.Cubepanion;
import org.cubepanion.core.versionlinkers.ChestFinderLink;
import org.jetbrains.annotations.NotNull;

@Singleton
@Implements(ChestFinderLink.class)
public class VersionedChestFinderLink extends ChestFinderLink {

  @Override
  public @NotNull List<ChestLocation> getChestLocations() {
    LocalPlayer player = Minecraft.getInstance().player;
    ClientPacketListener con = Minecraft.getInstance().getConnection();
    if (con == null || player == null) {
      return new ArrayList<>();
    }

    List<ChestLocation> out = new ArrayList<>();
    ChunkPos pos = player.chunkPosition();
    ClientLevel level = Minecraft.getInstance().getConnection().getLevel();
    int range = Cubepanion.get().configuration().getQolConfig().getRange().get();
    for (int x = -range; x <= range; x++) {
      for (int y = -range; y <= range; y++) {
        LevelChunk chunk = level.getChunk(pos.x + x, pos.z + y);
        for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
          if (entry.getValue().getType().equals(BlockEntityType.CHEST)) {
            ChestLocation loc = new ChestLocation(Cubepanion.season, entry.getKey().getX(),
                entry.getKey().getY(),
                entry.getKey().getZ());
            if (Cubepanion.chestLocations.contains(loc)) {
              out.add(loc);
            }
          }
        }
      }
    }
    return out;
  }
}

