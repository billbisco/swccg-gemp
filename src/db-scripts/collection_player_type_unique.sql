-- One binder row per player and collection type.
ALTER TABLE `collection`
  ADD UNIQUE INDEX `uq_collection_player_type` (`player_id`, `type`);
