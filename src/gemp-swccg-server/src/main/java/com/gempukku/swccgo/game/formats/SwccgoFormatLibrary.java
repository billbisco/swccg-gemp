package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.game.DeckSize;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.setup.GameOptions;
import com.gempukku.swccgo.game.setup.CardPool;
import com.gempukku.swccgo.game.setup.GameSetup;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
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
    // #144: retired format codes that are a base format with game options, e.g. open_no_shields = Open + Shields: none
    private final Map<String, String> _optionAliasBase = new HashMap<String, String>();
    private final Map<String, Map<String, String>> _optionAliasOptions = new HashMap<String, Map<String, String>>();
    private final Map<String, SwccgFormat> _optionAliasFormats = new ConcurrentHashMap<String, SwccgFormat>();
    private SwccgCardBlueprintLibrary _cardLibrary;
    /** Per base format, per chosen size: the default game timer and tenets link that size uses, when they differ. */
    private Map<String, Map<Integer, JSONObject>> _deckSizeVariants = new HashMap<String, Map<Integer, JSONObject>>();
    private final Map<String, SwccgFormat> _deckSizeFormats = new ConcurrentHashMap<String, SwccgFormat>();

    public SwccgoFormatLibrary(SwccgCardBlueprintLibrary library) {
        _cardLibrary = library;
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
                    format.setTextStates(Boolean.TRUE.equals(formatDef.get("textStates")));

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
                    JSONObject optionAliases = (JSONObject) formatDef.get("optionAliases");
                    if (optionAliases != null) {
                        for (Object aliasObj : optionAliases.entrySet()) {
                            Map.Entry<?, ?> alias = (Map.Entry<?, ?>) aliasObj;
                            Map<String, String> options = new java.util.LinkedHashMap<String, String>();
                            for (Object optionObj : ((JSONObject) alias.getValue()).entrySet()) {
                                Map.Entry<?, ?> option = (Map.Entry<?, ?>) optionObj;
                                options.put((String) option.getKey(), String.valueOf(option.getValue()));
                            }
                            _optionAliasBase.put((String) alias.getKey(), formatCode);
                            _optionAliasOptions.put((String) alias.getKey(), java.util.Collections.unmodifiableMap(options));
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
        if (_optionAliasBase.containsKey(formatCode))
            return _optionAliasFormats.computeIfAbsent(formatCode, code -> effectiveFormat(GameSetup.of(
                    getFormat(_optionAliasBase.get(code)), _optionAliasOptions.get(code), com.gempukku.swccgo.game.setup.GameOption.Scope.CASUAL)));
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
    private static int intOption(GameSetup setup, String optionId, SwccgFormat format) {
        String value = setup.get(optionId, format);
        return value == null ? 1 : Integer.parseInt(value);
    }

    public SwccgFormat effectiveFormat(GameSetup setup) {
        SwccgFormat format = getFormat(setup.getFormatCode());
        if (format == null)
            return null;
        String deckSize = setup.getChosen(GameOptions.DECK_SIZE);
        SwccgFormat sized = deckSize == null ? format : getFormat(DeckSize.code(format.getBaseCode(), Integer.parseInt(deckSize)));
        if (sized == null)
            return null;
        // deck rules the table's options add (#142/#144)
        DeckRules rules = new DeckRules(
                "true".equals(setup.get(GameOptions.NO_SHIELDS, format)),
                "true".equals(setup.get(GameOptions.OBJECTIVE_OPTIONAL, format)) && !format.allowsMultipleObjectives(),
                "true".equals(setup.get(GameOptions.SHIELDS_NO_PLAY_LIMIT, format)),
                intOption(setup, GameOptions.STARTING_EFFECTS, format),
                "true".equals(setup.get(GameOptions.FOUR_TWENTY, format)));
        SwccgFormat withRules = rules.isNone() ? sized : new DeckRulesSwccgFormat(sized, rules, _cardLibrary);
        // a tournament's custom card pool (#222): the event's sets and card exceptions on top of the rest
        CardPool pool = setup.getPool();
        return pool.isNone() ? withRules : new CustomPoolSwccgFormat(withRules, pool, _cardLibrary);
    }

    /**
     * Checks a custom card pool (#222) before an event is created with it: every set and card must be one GEMP
     * supports, playtesting cards only in a playtesting format, and no card may be both allowed and banned (on any
     * printing). Reports every problem at once.
     *
     * @throws IllegalArgumentException listing the problems, for the organizer
     */
    public void checkPool(SwccgFormat format, CardPool pool) {
        if (pool == null || pool.isNone())
            return;
        List<String> problems = new ArrayList<>();
        if (pool.hasSets()) {
            if (pool.getSets().isEmpty())
                problems.add("Choose at least one set for the custom card pool");
            for (Integer set : pool.getSets()) {
                if (!isSupportedSet(set))
                    problems.add("Set " + set + " is not a set GEMP supports");
                else if (isPlaytestingSet(set) && !format.isPlaytesting())
                    problems.add("Set " + set + " is a playtesting set and can only be used in a playtesting format");
            }
        }
        for (List<String> ids : java.util.Arrays.asList(pool.getAllowed(), pool.getBanned())) {
            for (String id : ids) {
                if (_cardLibrary.getSwccgoCardBlueprint(_cardLibrary.getBaseBlueprintId(id)) == null)
                    problems.add("Card " + id + " is not a card GEMP supports");
                else if (isPlaytestingSet(setOf(id)) && !format.isPlaytesting())
                    problems.add("Card " + id + " is a playtesting card and can only be used in a playtesting format");
            }
        }
        Set<String> bannedPrintings = CustomPoolSwccgFormat.printings(_cardLibrary, pool.getBanned());
        for (String id : pool.getAllowed()) {
            for (String printing : CustomPoolSwccgFormat.printingsOf(_cardLibrary, id)) {
                if (bannedPrintings.contains(printing)) {
                    problems.add("Card " + cardLabel(id) + " is both allowed and banned; choose one");
                    break;
                }
            }
        }
        if (!problems.isEmpty())
            throw new IllegalArgumentException("Custom card pool: " + String.join("; ", problems) + ".");
    }

    private boolean isSupportedSet(int set) {
        return set > 0 && _cardLibrary.getSwccgoCardBlueprint(set + "_1") != null;
    }

    private static boolean isPlaytestingSet(Integer set) {
        return set != null && set >= 501 && set < 501 + com.gempukku.swccgo.common.CardCounts.PLAYTESTING_SETS_CARD_COUNTS.length;
    }

    private static Integer setOf(String id) {
        int underscore = id.indexOf('_');
        if (underscore <= 0)
            return null;
        try {
            return Integer.parseInt(id.substring(0, underscore));
        } catch (NumberFormatException exp) {
            return null;
        }
    }

    /**
     * A card as the rules summary names it (#222): full title, side, set and GEMP id, so cards with the same title (a
     * Light and Dark version, an original and a Virtual one) are told apart: {@code "Han Solo (Light, set 1, 1_80)"}.
     */
    public String cardLabel(String id) {
        SwccgCardBlueprint blueprint = _cardLibrary.getSwccgoCardBlueprint(_cardLibrary.getBaseBlueprintId(id));
        if (blueprint == null)
            return id;
        String side = blueprint.getSide() == null ? "" : blueprint.getSide().getHumanReadable() + ", ";
        Integer set = setOf(id);
        return com.gempukku.swccgo.logic.GameUtils.getFullName(blueprint) + " (" + side
                + (set != null ? "set " + set + ", " : "") + id + ")";
    }

    private volatile List<String[]> _searchIndex;

    /** Every card a pool can name, as {id, lower-case title}, built once (#222 card search). */
    private List<String[]> searchIndex() {
        List<String[]> index = _searchIndex;
        if (index != null)
            return index;
        index = new ArrayList<>();
        int[][] ranges = {
                com.gempukku.swccgo.common.CardCounts.FULL_SETS_CARD_COUNTS,
                com.gempukku.swccgo.common.CardCounts.REFLECTIONS_GOLD_CARD_COUNTS,
                com.gempukku.swccgo.common.CardCounts.PREMIUM_SETS_CARD_COUNTS,
                com.gempukku.swccgo.common.CardCounts.VIRTUAL_SETS_CARD_COUNTS,
                com.gempukku.swccgo.common.CardCounts.VIRTUAL_PREMIUM_SETS_CARD_COUNTS,
                com.gempukku.swccgo.common.CardCounts.LEGACY_SETS_CARD_COUNTS};
        int[] firstSets = {1, 15, 101, 200, 301, 601};
        for (int r = 0; r < ranges.length; r++) {
            for (int i = 0; i < ranges[r].length; i++) {
                for (int j = 1; j <= ranges[r][i]; j++) {
                    String id = (firstSets[r] + i) + "_" + j;
                    SwccgCardBlueprint blueprint;
                    try {
                        blueprint = _cardLibrary.getSwccgoCardBlueprint(id);
                    } catch (RuntimeException exp) {
                        blueprint = null;
                    }
                    if (blueprint != null && blueprint.getTitle() != null)
                        index.add(new String[] {id, blueprint.getTitle().toLowerCase(java.util.Locale.ROOT)});
                }
            }
        }
        _searchIndex = index;
        return index;
    }

    /**
     * Cards for the organizer's pool search (#222): ids whose title contains {@code query} (case-insensitive), or the
     * card with that exact id. At most {@code limit}, in set order. Labels come from {@link #cardLabel}.
     */
    public List<String> searchCards(String query, int limit) {
        List<String> result = new ArrayList<>();
        if (query == null || query.trim().length() < 2)
            return result;
        String q = query.trim().toLowerCase(java.util.Locale.ROOT);
        if (q.matches("\\d+_\\d+") && _cardLibrary.getSwccgoCardBlueprint(q) != null) {
            result.add(q);
            return result;
        }
        for (String[] entry : searchIndex()) {
            if (entry[1].contains(q)) {
                result.add(entry[0]);
                if (result.size() >= limit)
                    break;
            }
        }
        return result;
    }

    /**
     * The Create Tournament form's live preview of a pool (#222): the same lines the saved event's summary shows, after
     * the same checks creating the event runs, so the preview and the saved event agree.
     *
     * @throws IllegalArgumentException for an unknown format or a pool the event would refuse
     */
    public List<String> previewPool(String formatCode, CardPool pool) {
        SwccgFormat format = getFormat(DeckSize.baseCode(formatCode));
        if (format == null)
            throw new IllegalArgumentException("This format is not supported: " + formatCode);
        SwccgFormat base = getFormat(format.getBaseCode());
        checkPool(base != null ? base : format, pool);
        return describePool(GameSetup.defaults(format.getBaseCode()).withPool(pool));
    }

    /**
     * The public rules summary of an event's card pool (#222), one line per part, shown to players before they join and
     * in the event while it runs. Empty when the event plays its format's own pool.
     */
    public List<String> describePool(GameSetup setup) {
        List<String> lines = new ArrayList<>();
        if (setup == null || setup.getPool().isNone())
            return lines;
        CardPool pool = setup.getPool();
        SwccgFormat format = getFormat(setup.getFormatCode());
        String formatName = format != null ? format.getName() : setup.getFormatCode();
        if (pool.hasSets()) {
            List<String> sets = new ArrayList<>();
            for (Integer set : pool.getSets())
                sets.add(String.valueOf(set));
            lines.add("Sets: " + String.join(", ", sets) + " (instead of " + formatName + "'s sets)");
        } else {
            lines.add("Sets: " + formatName + "'s sets");
        }
        for (String id : pool.getAllowed())
            lines.add("Allowed: " + cardLabel(id));
        for (String id : pool.getBanned())
            lines.add("Banned: " + cardLabel(id));
        return lines;
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
        // a retired code that stands for options (#144: open_no_shields) gives the setup those options
        Map<String, String> aliasOptions = _optionAliasOptions.get(formatCode);
        if (aliasOptions != null)
            for (Map.Entry<String, String> option : aliasOptions.entrySet())
                if (setup.getChosen(option.getKey()) == null)
                    setup = setup.with(option.getKey(), option.getValue());
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
