package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.game.DeckSize;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.setup.GameOptions;
import com.gempukku.swccgo.game.setup.GameSetup;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SwccgoFormatLibrary {
    private Map<String, SwccgFormat> _allFormats = new LinkedHashMap<String, SwccgFormat>();
    private Map<String, SwccgFormat> _hallFormats = new LinkedHashMap<String, SwccgFormat>();
    private Map<String, String> _formatStamps = new HashMap<String, String>();
    /** Retired size-only format codes and the {@code <format>@<size>} code each now means (#90). */
    private Map<String, String> _aliases = new HashMap<String, String>();
    /** Per base format, per chosen size: the default game timer and tenets link that size uses, when they differ. */
    private Map<String, Map<Integer, JSONObject>> _deckSizeVariants = new HashMap<String, Map<Integer, JSONObject>>();
    private final Map<String, SwccgFormat> _deckSizeFormats = new ConcurrentHashMap<String, SwccgFormat>();

    public SwccgoFormatLibrary(SwccgCardBlueprintLibrary library) {
        try {
            final InputStreamReader reader = new InputStreamReader(SwccgoFormatLibrary.class.getResourceAsStream("/swccgFormats.json"), "UTF-8");
            try {
                JSONParser parser = new JSONParser();
                JSONArray object = (JSONArray) parser.parse(reader);
                for (Object formatDefObj : object) {
                    JSONObject formatDef = (JSONObject) formatDefObj;
                    String formatCode = (String) formatDef.get("code");
                    String name = (String) formatDef.get("name");
                    Boolean downloadBattlegroundRule = (Boolean) formatDef.get("downloadBattlegroundRule");
                    if (downloadBattlegroundRule == null)
                        downloadBattlegroundRule = false;

                    Boolean loreHeroicRule = (Boolean) formatDef.get("loreHeroicRule");
                    if (loreHeroicRule == null)
                        loreHeroicRule = false;

                    Boolean jpSealedRule = (Boolean) formatDef.get("jpSealedRule");
                    if (jpSealedRule == null)
                        jpSealedRule = false;

                    Boolean playtesting = (Boolean) formatDef.get("playtesting");
                    if (playtesting == null)
                        playtesting = false;

                    final DefaultSwccgFormat format = new DefaultSwccgFormat(library, name, downloadBattlegroundRule, loreHeroicRule, jpSealedRule, playtesting);
                    format.setCode(formatCode);
                    format.setStartingCredits(Boolean.TRUE.equals(formatDef.get("startingCredits")));

                    Boolean skipFormatPool = (Boolean) formatDef.get("skipFormatPool");
                    if (skipFormatPool != null && skipFormatPool)
                        format.setSkipFormatPool(true);
                    format.setMultipleObjectives(Boolean.TRUE.equals(formatDef.get("multipleObjectives")));

                    Long minDeckSize = (Long) formatDef.get("minDeckSize");
                    Long maxDeckSize = (Long) formatDef.get("maxDeckSize");
                    Long deckSize = (Long) formatDef.get("deckSize");
                    if (minDeckSize != null && maxDeckSize != null) {
                        format.setDeckSizeRange(minDeckSize.intValue(), maxDeckSize.intValue());
                    } else {
                        if (deckSize == null)
                            deckSize = 60L;
                        format.setRequiredDeckSize(deckSize.intValue());
                    }

                    Long maxDeckSizeOption = (Long) formatDef.get("maxDeckSizeOption");
                    if (maxDeckSizeOption != null)
                        format.setMaxDeckSizeOption(maxDeckSizeOption.intValue());

                    Long defaultGameTimerMinutes = (Long) formatDef.get("defaultGameTimerMinutes");
                    if (defaultGameTimerMinutes == null)
                        defaultGameTimerMinutes = 60L;
                    format.setDefaultGameTimerMinutes(defaultGameTimerMinutes.intValue());


                    JSONArray sets = (JSONArray) formatDef.get("set");
                    if (sets != null) {
                        for (Object set : sets)
                            format.addValidSet(((Number) set).intValue());
                    }

                    JSONArray validCards = (JSONArray) formatDef.get("valid");
                    if (validCards != null)
                        for (Object valid : validCards) {
                            format.addValidCard((String) valid);
                        }

                    JSONArray bannedCards = (JSONArray) formatDef.get("banned");
                    if (bannedCards != null)
                        for (Object bannedCard : bannedCards) {
                            format.addBannedCard((String) bannedCard);
                        }

                    String tenetsLink = (String) formatDef.get("tenetsLink");
                    if (tenetsLink != null) {
                        format.addTenetsLink(tenetsLink);
                    }

                    JSONArray bannedIcons = (JSONArray) formatDef.get("bannedIcons");
                    if (bannedIcons != null)
                        for (Object bannedIcon : bannedIcons) {
                            format.addBannedIcon((String) bannedIcon);
                        }

                    JSONArray bannedRarities = (JSONArray) formatDef.get("bannedRarities");
                    if (bannedRarities != null)
                        for (Object bannedRarity: bannedRarities) {
                            format.addBannedRarity((String) bannedRarity);
                        }

                    JSONArray restrictedCards = (JSONArray) formatDef.get("restricted");
                    if (restrictedCards != null)
                        for (Object restricted : restrictedCards) {
                            format.addRestrictedCard((String) restricted);
                        }

                    // #90: deck size is a setup setting. Old size-only codes become aliases of <format>@<size>.
                    JSONObject aliases = (JSONObject) formatDef.get("aliases");
                    if (aliases != null) {
                        for (Object aliasObj : aliases.entrySet()) {
                            Map.Entry<?, ?> alias = (Map.Entry<?, ?>) aliasObj;
                            int size = ((Number) alias.getValue()).intValue();
                            _aliases.put((String) alias.getKey(), DeckSize.code(formatCode, size));
                        }
                    }
                    JSONObject deckSizeVariants = (JSONObject) formatDef.get("deckSizeVariants");
                    if (deckSizeVariants != null) {
                        Map<Integer, JSONObject> bySize = new HashMap<Integer, JSONObject>();
                        for (Object variantObj : deckSizeVariants.entrySet()) {
                            Map.Entry<?, ?> variant = (Map.Entry<?, ?>) variantObj;
                            bySize.put(Integer.parseInt((String) variant.getKey()), (JSONObject) variant.getValue());
                        }
                        _deckSizeVariants.put(formatCode, bySize);
                    }

                    _allFormats.put(formatCode, format);

                    Boolean hallFormat = (Boolean) formatDef.get("hall");
                    if (hallFormat == null)
                        hallFormat = true;
                    if (hallFormat)
                        _hallFormats.put(formatCode, format);

                    _formatStamps.put(formatCode, hashRules(format.rulesStampSource()));
                }
            } catch (ParseException exp) {
                throw new RuntimeException("Problem loading Swccg formats", exp);
            } finally {
                reader.close();
            }
        } catch (IOException exp) {
            throw new RuntimeException("Problem loading Swccg formats", exp);
        }
    }

    public String stampFor(String formatCode) {
        String stamp = _formatStamps.get(formatCode);
        if (stamp != null)
            return stamp;
        SwccgFormat format = getFormat(formatCode);
        if (format instanceof DeckSizeSwccgFormat)
            return hashRules(((DeckSizeSwccgFormat) format).rulesStampSource());
        return format != null ? _formatStamps.get(format.getCode()) : null;
    }

    private static String hashRules(String source) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(source.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(16);
            for (int i = 0; i < 8; i++) {
                hex.append(String.format("%02x", hash[i]));
            }
            return hex.toString();
        } catch (Exception exp) {
            throw new RuntimeException("Problem hashing format rules", exp);
        }
    }

    public Map<String, SwccgFormat> getHallFormats() {
        return Collections.unmodifiableMap(_hallFormats);
    }

    public Map<String, SwccgFormat> getAllFormats() {
        return Collections.unmodifiableMap(_allFormats);
    }

    /**
     * The format for a code: a format from {@code swccgFormats.json}, a retired size-only code (an alias), or
     * {@code <format>@<size>} for a format played at a deck size chosen at setup (#90). Returns {@code null} for an
     * unknown format or an unsupported size.
     */
    public SwccgFormat getFormat(String formatCode) {
        if (formatCode == null)
            return null;
        SwccgFormat format = _allFormats.get(formatCode);
        if (format != null)
            return format;
        String alias = _aliases.get(formatCode);
        if (alias != null)
            return getFormat(alias);
        return getDeckSizeFormat(formatCode, _allFormats);
    }

    /**
     * Like {@link #getFormat(String)}, but only for formats offered in the hall: a hall format, or a hall format at a
     * chosen deck size.
     */
    public SwccgFormat getHallFormat(String formatCode) {
        if (formatCode == null)
            return null;
        SwccgFormat format = _hallFormats.get(formatCode);
        if (format != null)
            return format;
        return getDeckSizeFormat(formatCode, _hallFormats);
    }

    /**
     * The code for a format at another deck size it allows ({@code open@100} for a 100-card deck in Open), or
     * {@code null} when the size is within the format's own range or not one the format allows (#120). Used to offer a
     * deck that is legal only at its own size.
     */
    public String otherSizeCode(SwccgFormat format, int size) {
        if (format == null || format.isDeckSizeChosen())
            return null;
        if (size >= format.getMinimumDeckSize() && size <= format.getMaximumDeckSize())
            return null;
        if (!DeckSize.isSupported(format, size))
            return null;
        SwccgFormat sized = getFormat(DeckSize.code(format.getBaseCode(), size));
        return sized != null && sized.isDeckSizeChosen() ? sized.getCode() : null;
    }

    /** The code for a format played at a deck size; the format's own code when the size is its default (#90). */
    public String codeWithDeckSize(String formatCode, Integer deckSize) {
        SwccgFormat format = getFormat(formatCode);
        if (format == null || deckSize == null)
            return format == null ? formatCode : format.getCode();
        SwccgFormat sized = getFormat(DeckSize.code(format.getBaseCode(), deckSize));
        if (sized == null)
            throw new IllegalArgumentException(DeckSize.rangeMessage(format));
        return sized.getCode();
    }

    /**
     * The format a game with this setup plays (#116): the setup's format, at the chosen deck size if there is one.
     * Returns {@code null} for an unknown format or an unsupported size.
     */
    public SwccgFormat effectiveFormat(GameSetup setup) {
        SwccgFormat format = getFormat(setup.getFormatCode());
        if (format == null)
            return null;
        String deckSize = setup.getChosen(GameOptions.DECK_SIZE);
        if (deckSize == null)
            return format;
        return getFormat(DeckSize.code(format.getBaseCode(), Integer.parseInt(deckSize)));
    }

    /**
     * The setup of a stored row (#116): its {@code game_setup} JSON, or the format's defaults for rows from before it.
     * A retired size-only code ({@code open_100_card}) or a {@code <format>@<size>} key becomes its base format with
     * that deck size, so old leagues, tournaments and games keep their size.
     */
    public GameSetup storedSetup(String formatCode, String setupJson) {
        SwccgFormat format = getFormat(formatCode);
        String baseCode = format != null ? format.getBaseCode() : formatCode;
        GameSetup setup = GameSetup.fromJson(setupJson, baseCode).forFormat(baseCode);
        if (format != null && format.isDeckSizeChosen() && setup.getChosen(GameOptions.DECK_SIZE) == null)
            setup = setup.with(GameOptions.DECK_SIZE, String.valueOf(format.getRequiredDeckSize()));
        return setup;
    }

    private SwccgFormat getDeckSizeFormat(String formatCode, Map<String, SwccgFormat> baseFormats) {
        Integer size;
        try {
            size = DeckSize.chosenSize(formatCode);
        } catch (IllegalArgumentException exp) {
            return null;
        }
        if (size == null)
            return null;
        String baseCode = DeckSize.baseCode(formatCode);
        SwccgFormat base = baseFormats.get(baseCode);
        if (!(base instanceof DefaultSwccgFormat) || !DeckSize.isSupported(base, size))
            return null;
        // The format's own fixed size is just the format (a table left at its default plays as today).
        if (base.getMinimumDeckSize() == size && base.getMaximumDeckSize() == size)
            return base;
        return _deckSizeFormats.computeIfAbsent(DeckSize.code(baseCode, size), code -> {
            JSONObject variant = _deckSizeVariants.getOrDefault(baseCode, Collections.emptyMap()).get(size);
            Integer timer = null;
            String tenetsLink = null;
            if (variant != null) {
                Number timerValue = (Number) variant.get("defaultGameTimerMinutes");
                timer = timerValue == null ? null : timerValue.intValue();
                tenetsLink = (String) variant.get("tenetsLink");
            }
            return new DeckSizeSwccgFormat((DefaultSwccgFormat) base, size, timer, tenetsLink);
        });
    }
}
