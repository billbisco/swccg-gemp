package com.gempukku.swccgo.db;

import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DbDeckDAO implements DeckDAO {
    private DbAccess _dbAccess;
    private SwccgCardBlueprintLibrary _library;
    private volatile boolean _schemaReady;

    public DbDeckDAO(DbAccess dbAccess, SwccgCardBlueprintLibrary library) {
        _dbAccess = dbAccess;
        _library = library;
    }

    public synchronized SwccgDeck getDeckForPlayer(Player player, String name) {
        return getPlayerDeck(player.getId(), name);
    }

    public synchronized void saveDeckForPlayer(Player player, String name, SwccgDeck deck) {
        ensureSchema();
        boolean newDeck = getPlayerDeck(player.getId(), name) == null;
        storeDeckToDB(player.getId(), name, deck, newDeck);
    }

    public synchronized void deleteDeckForPlayer(Player player, String name) {
        try {
            deleteDeckFromDB(player.getId(), name);
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to delete player deck from DB", exp);
        }
    }

    public synchronized SwccgDeck renameDeck(Player player, String oldName, String newName) {
        SwccgDeck deck = getDeckForPlayer(player, oldName);
        if (deck == null)
            return null;
        saveDeckForPlayer(player, newName, deck);
        deleteDeckForPlayer(player, oldName);

        return deck;
    }

    public synchronized Set<String> getPlayerDeckNames(Player player) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select name from deck where player_id=?");
                try {
                    statement.setInt(1, player.getId());
                    ResultSet rs = statement.executeQuery();
                    try {
                        Set<String> result = new HashSet<String>();

                        while (rs.next())
                            result.add(rs.getString(1));

                        return result;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to load player decks from DB", exp);
        }
    }

    public synchronized List<SwccgDeck> getAllDecksForPlayer(Player player) {
        ensureSchema();
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "select name, contents, valid_formats, formats_revision, source_collection from deck where player_id=?");
                try {
                    statement.setInt(1, player.getId());
                    ResultSet rs = statement.executeQuery();
                    try {
                        List<SwccgDeck> result = new ArrayList<SwccgDeck>();
                        while (rs.next()) {
                            result.add(deckFromRow(rs.getString(1), rs.getString(2),
                                    rs.getString(3), rs.getString(4), rs.getString(5)));
                        }
                        return result;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to load player decks from DB", exp);
        }
    }

    public synchronized void updateDeckIndex(Player player, String name, String validFormats, String formatsRevision,
            String sourceCollection) {
        ensureSchema();
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "update deck set valid_formats=?, formats_revision=?, source_collection=? where player_id=? and name=?");
                try {
                    statement.setString(1, validFormats);
                    statement.setString(2, formatsRevision);
                    statement.setString(3, sourceCollection);
                    statement.setInt(4, player.getId());
                    statement.setString(5, name);
                    statement.execute();
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to update deck format index", exp);
        }
    }

    private SwccgDeck getPlayerDeck(int playerId, String name) {
        ensureSchema();
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "select contents, valid_formats, formats_revision, source_collection from deck where player_id=? and name=?");
                try {
                    statement.setInt(1, playerId);
                    statement.setString(2, name);
                    ResultSet rs = statement.executeQuery();
                    try {
                        if (rs.next())
                            return deckFromRow(name, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));

                        return null;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }

        } catch (SQLException exp) {
            throw new RuntimeException("Unable to load player decks from DB", exp);
        }
    }

    private SwccgDeck deckFromRow(String name, String contents, String validFormats, String formatsRevision,
            String sourceCollection) {
        SwccgDeck deck = buildDeckFromContents(name, contents);
        if (deck == null) {
            return null;
        }
        deck.setValidFormats(validFormats);
        deck.setFormatsRevision(formatsRevision);
        deck.setSourceCollection(sourceCollection);
        return deck;
    }

    private void storeDeckToDB(int playerId, String name, SwccgDeck deck, boolean newDeck) {
        String contents = DeckSerialization.buildContentsFromDeck(deck);
        try {
            if (newDeck)
                storeDeckInDB(playerId, name, contents, deck);
            else
                updateDeckInDB(playerId, name, contents, deck);
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to store player deck to DB", exp);
        }
    }

    public synchronized SwccgDeck buildDeckFromContents(String deckName, String contents) {
        return DeckSerialization.buildDeckFromContents(deckName, contents, _library);
    }

    private void deleteDeckFromDB(int playerId, String name) throws SQLException {
        Connection connection = _dbAccess.getDataSource().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("delete from deck where player_id=? and name=?");
            try {
                statement.setInt(1, playerId);
                statement.setString(2, name);
                statement.execute();
            } finally {
                statement.close();
            }
        } finally {
            connection.close();
        }
    }

    private void storeDeckInDB(int playerId, String name, String contents, SwccgDeck deck) throws SQLException {
        Connection connection = _dbAccess.getDataSource().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "insert into deck (player_id, name, contents, valid_formats, formats_revision, source_collection) values (?, ?, ?, ?, ?, ?)");
            try {
                statement.setInt(1, playerId);
                statement.setString(2, name);
                statement.setString(3, contents);
                statement.setString(4, deck.getValidFormats());
                statement.setString(5, deck.getFormatsRevision());
                statement.setString(6, deck.getSourceCollection());
                statement.execute();
            } finally {
                statement.close();
            }
        } finally {
            connection.close();
        }
    }

    private void updateDeckInDB(int playerId, String name, String contents, SwccgDeck deck) throws SQLException {
        Connection connection = _dbAccess.getDataSource().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "update deck set contents=?, valid_formats=?, formats_revision=?, source_collection=? where player_id=? and name=?");
            try {
                statement.setString(1, contents);
                statement.setString(2, deck.getValidFormats());
                statement.setString(3, deck.getFormatsRevision());
                statement.setString(4, deck.getSourceCollection());
                statement.setInt(5, playerId);
                statement.setString(6, name);
                statement.execute();
            } finally {
                statement.close();
            }
        } finally {
            connection.close();
        }
    }

    private void ensureSchema() {
        if (_schemaReady) {
            return;
        }
        synchronized (this) {
            if (_schemaReady) {
                return;
            }
            try {
                Connection connection = _dbAccess.getDataSource().getConnection();
                try {
                    Statement statement = connection.createStatement();
                    try {
                        statement.execute("ALTER TABLE deck ADD COLUMN IF NOT EXISTS valid_formats TEXT CHARACTER SET utf8 COLLATE utf8_bin NULL");
                        statement.execute("ALTER TABLE deck ADD COLUMN IF NOT EXISTS formats_revision VARCHAR(64) CHARACTER SET utf8 COLLATE utf8_bin NULL");
                        statement.execute("ALTER TABLE deck ADD COLUMN IF NOT EXISTS source_collection VARCHAR(80) CHARACTER SET utf8 COLLATE utf8_bin NULL");
                    } finally {
                        statement.close();
                    }
                } finally {
                    connection.close();
                }
                _schemaReady = true;
            } catch (SQLException exp) {
                throw new RuntimeException("Unable to add deck format index columns", exp);
            }
        }
    }
}
