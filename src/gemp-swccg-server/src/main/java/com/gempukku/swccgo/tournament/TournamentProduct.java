package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.MutableCardCollection;
import com.gempukku.swccgo.league.SealedLeagueProduct;
import com.gempukku.swccgo.league.SealedLeagueType;
import com.gempukku.swccgo.packagedProduct.ProductName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TournamentProduct {
    public static final String TYPE_CONSTRUCTED = "constructed";
    public static final String TYPE_SEALED = "sealed";
    public static final String TYPE_DRAFT = "draft";
    public static final String TYPE_CUBE = "cube";
    public static final String MODE_SOLO = "solo";
    public static final String MODE_LIVE = "live";

    public static final int DECK_BUILD_MS = 30 * 60 * 1000;
    public static final int SOLO_TABLE_SEATS = 8;

    private static final Map<String, TournamentProduct> PRODUCTS = new LinkedHashMap<String, TournamentProduct>();

    static {
        add(sealed(SealedLeagueType.PREMIERE_ANH_SEALED));
        add(sealed(SealedLeagueType.JP_SEALED));
        add(sealed(SealedLeagueType.ENDOR_DSII_SEALED));
        add(sealed(SealedLeagueType.REFLECTIONS_SEALED));
        add(sealed(SealedLeagueType.EPISODE_I_SEALED));
        add(sealed(SealedLeagueType.ALL_OF_THE_JEDI_SEALED));
        add(sealed(SealedLeagueType.NOVELTY_SEALED));
        add(sealed(SealedLeagueType.WATTOS_CUBE_WITH_OBJECTIVE_PACKS).display("Watto's Cube Objectives"));
        add(sealed(SealedLeagueType.WATTOS_CUBE_WITH_FIXED).display("Watto's Cube Fixed"));

        add(paperDraft("premiere_anh_draft", "Premiere - A New Hope Draft",
                SealedLeagueType.PREMIERE_ANH_SEALED.getFormatCode(),
                SealedLeagueType.PREMIERE_ANH_SEALED.getSealedCode(),
                packs(ProductName.PREMIERE_BOOSTER_PACK, 4, ProductName.A_NEW_HOPE_BOOSTER_PACK, 1)));
        add(paperDraft("jp_draft", "Jabba's Palace Draft",
                SealedLeagueType.JP_SEALED.getFormatCode(),
                SealedLeagueType.JP_SEALED.getSealedCode(),
                packs(ProductName.JABBAS_PALACE_BOOSTER_PACK, 6)));
        add(paperDraft("endor_dsII_draft", "Endor - Death Star II Draft",
                SealedLeagueType.ENDOR_DSII_SEALED.getFormatCode(),
                SealedLeagueType.ENDOR_DSII_SEALED.getSealedCode(),
                packs(ProductName.ENDOR_BOOSTER_PACK_NO_RANDOM_FOIL, 2)));
        add(paperDraft("reflections_draft", "Reflections Draft",
                SealedLeagueType.REFLECTIONS_SEALED.getFormatCode(),
                SealedLeagueType.REFLECTIONS_SEALED.getSealedCode(),
                packs(ProductName.REFLECTIONS_BOOSTER_PACK, 4, ProductName.REFLECTIONS_II_BOOSTER_PACK, 4)));
        add(paperDraft("episode_i_draft", "Episode I Draft",
                SealedLeagueType.EPISODE_I_SEALED.getFormatCode(),
                SealedLeagueType.EPISODE_I_SEALED.getSealedCode(),
                packs(ProductName.TATOOINE_BOOSTER_PACK_EPISODE_I_ONLY, 3,
                        ProductName.CORUSCANT_BOOSTER_PACK_EPISODE_I_ONLY, 3,
                        ProductName.THEED_PALACE_BOOSTER_PACK, 3)));
        add(paperDraft("all_of_the_jedi_draft", "All Of The Jedi Draft",
                SealedLeagueType.ALL_OF_THE_JEDI_SEALED.getFormatCode(),
                SealedLeagueType.ALL_OF_THE_JEDI_SEALED.getSealedCode(),
                packs(ProductName.PREMIERE_BOOSTER_PACK, 1, ProductName.A_NEW_HOPE_BOOSTER_PACK, 1,
                        ProductName.HOTH_BOOSTER_PACK, 1, ProductName.DAGOBAH_BOOSTER_PACK, 1,
                        ProductName.CLOUD_CITY_BOOSTER_PACK, 1, ProductName.JABBAS_PALACE_BOOSTER_PACK, 1,
                        ProductName.SPECIAL_EDITION_BOOSTER_PACK, 1, ProductName.ENDOR_BOOSTER_PACK, 1,
                        ProductName.DEATH_STAR_II_BOOSTER_PACK, 1, ProductName.TATOOINE_BOOSTER_PACK, 1,
                        ProductName.CORUSCANT_BOOSTER_PACK, 1, ProductName.THEED_PALACE_BOOSTER_PACK, 1,
                        ProductName.REFLECTIONS_BOOSTER_PACK, 1, ProductName.REFLECTIONS_II_BOOSTER_PACK, 1,
                        ProductName.REFLECTIONS_III_BOOSTER_PACK, 1)));
        add(paperDraft("novelty_draft", "Novelty - Space Draft",
                SealedLeagueType.NOVELTY_SEALED.getFormatCode(),
                SealedLeagueType.NOVELTY_SEALED.getSealedCode(),
                packs(ProductName.A_NEW_HOPE_BOOSTER_PACK, 5, ProductName.DEATH_STAR_II_BOOSTER_PACK, 5)));

        add(jsonCube("basic_premds2_draft", "Premiere - Death Star II Cube", 6));
        add(jsonCube("basic_premv13_draft", "Premiere - VS13 Cube", 5));
        add(jsonCube("holocron_premv25_draft", "Premiere - VS25 Cube", 5));
        add(jsonCube("c7obj_draft", "Cube v7 + Objectives", 4));
        add(wattoCube());
    }

    private final String _code;
    private final String _kind;
    private String _displayName;
    private final String _formatCode;
    private final String _sealedLeagueCode;
    private final List<String> _defaultPacks;
    private final String _cubeDraftType;
    private final int _liveMaxPlayers;
    private final boolean _jsonCube;
    private final boolean _wattoCube;

    private TournamentProduct(String code, String kind, String displayName, String formatCode, String sealedLeagueCode,
                              List<String> defaultPacks, String cubeDraftType, int liveMaxPlayers,
                              boolean jsonCube, boolean wattoCube) {
        _code = code;
        _kind = kind;
        _displayName = displayName;
        _formatCode = formatCode;
        _sealedLeagueCode = sealedLeagueCode;
        _defaultPacks = defaultPacks == null ? Collections.<String>emptyList() : defaultPacks;
        _cubeDraftType = cubeDraftType;
        _liveMaxPlayers = liveMaxPlayers;
        _jsonCube = jsonCube;
        _wattoCube = wattoCube;
    }

    private TournamentProduct display(String displayName) {
        _displayName = displayName;
        return this;
    }

    public String getCode() {
        return _code;
    }

    public String getKind() {
        return _kind;
    }

    public String getDisplayName() {
        return _displayName;
    }

    public String getFormatCode() {
        return _formatCode;
    }

    public String getSealedLeagueCode() {
        return _sealedLeagueCode;
    }

    public String getCubeDraftType() {
        return _cubeDraftType;
    }

    public int getLiveMaxPlayers() {
        return _liveMaxPlayers;
    }

    public boolean isJsonCube() {
        return _jsonCube;
    }

    public boolean isWattoCube() {
        return _wattoCube;
    }

    public boolean isLimited() {
        return !TYPE_CONSTRUCTED.equals(_kind);
    }

    public List<String> defaultPacks() {
        return _defaultPacks;
    }

    public int defaultPackCount() {
        if (_jsonCube)
            return 6;
        if (_wattoCube)
            return 4;
        return Math.max(1, _defaultPacks.size());
    }

    public List<String> packsForCount(int packCount) {
        int n = packCount > 0 ? packCount : defaultPackCount();
        if (_jsonCube)
            return _defaultPacks;
        if (_wattoCube) {
            List<String> packs = new ArrayList<String>();
            for (int i = 0; i < n; i++)
                packs.add(ProductName.CUBE_DRAFT_PACK_DARK);
            for (int i = 0; i < n; i++)
                packs.add(ProductName.CUBE_DRAFT_PACK_LIGHT);
            return packs;
        }
        if (n <= 1 || n == _defaultPacks.size())
            return new ArrayList<String>(_defaultPacks);
        if (n < _defaultPacks.size())
            return new ArrayList<String>(_defaultPacks.subList(0, n));
        List<String> doubled = new ArrayList<String>(_defaultPacks);
        doubled.addAll(_defaultPacks);
        return doubled;
    }

    public List<Integer> packChoices() {
        if (_jsonCube || _wattoCube)
            return cubePackChoices();
        int def = defaultPackCount();
        return Arrays.asList(def, Math.max(def * 2, def + 1));
    }

    public CardCollection sealedKit(SealedLeagueProduct product) {
        if (_sealedLeagueCode == null)
            return new DefaultCardCollection();
        return product.getCollectionForSeries(_sealedLeagueCode, 0);
    }

    public CardCollection draftKeep(SealedLeagueProduct product) {
        if (_wattoCube) {
            MutableCardCollection keep = new DefaultCardCollection(true);
            keep.addItem(ProductName.CUBE_OBJECTIVE_PACK_DARK, 2);
            keep.addItem(ProductName.CUBE_OBJECTIVE_PACK_LIGHT, 2);
            return keep;
        }
        if (_sealedLeagueCode == null)
            return new DefaultCardCollection();
        CardCollection series1 = product.getCollectionForSeries(_sealedLeagueCode, 0);
        MutableCardCollection keep = new DefaultCardCollection(series1);
        for (String pack : uniquePackNames(_defaultPacks)) {
            int count = series1.getItemCount(pack);
            if (count > 0)
                keep.removeItem(pack, count);
        }
        return keep;
    }

    public static TournamentProduct get(String code) {
        return PRODUCTS.get(code);
    }

    public static List<TournamentProduct> list(String kind) {
        List<TournamentProduct> result = new ArrayList<TournamentProduct>();
        for (TournamentProduct product : PRODUCTS.values()) {
            if (kind == null || kind.equals(product._kind))
                result.add(product);
        }
        return result;
    }

    public static boolean isLimitedType(String type) {
        return TYPE_SEALED.equals(type) || TYPE_DRAFT.equals(type) || TYPE_CUBE.equals(type);
    }

    public static boolean needsDraftMode(String type) {
        return TYPE_DRAFT.equals(type) || TYPE_CUBE.equals(type);
    }

    public static String normalizeType(String type) {
        if (type == null || type.isEmpty())
            return TYPE_CONSTRUCTED;
        String lower = type.toLowerCase();
        if (TYPE_SEALED.equals(lower) || TYPE_DRAFT.equals(lower) || TYPE_CUBE.equals(lower) || TYPE_CONSTRUCTED.equals(lower))
            return lower;
        return null;
    }

    public static String normalizeMode(String mode) {
        if (mode == null || mode.isEmpty())
            return MODE_SOLO;
        String lower = mode.toLowerCase();
        if (MODE_LIVE.equals(lower))
            return MODE_LIVE;
        return MODE_SOLO;
    }

    private static void add(TournamentProduct product) {
        PRODUCTS.put(product._code, product);
    }

    private static TournamentProduct sealed(SealedLeagueType type) {
        return new TournamentProduct(type.getSealedCode(), TYPE_SEALED, type.getHumanReadable(),
                type.getFormatCode(), type.getSealedCode(), Collections.<String>emptyList(),
                null, 128, false, false);
    }

    private static TournamentProduct paperDraft(String code, String display, String formatCode, String sealedCode,
                                                List<String> packs) {
        return new TournamentProduct(code, TYPE_DRAFT, display, formatCode, sealedCode, packs,
                null, 128, false, false);
    }

    private static TournamentProduct jsonCube(String code, String display, int liveMax) {
        return new TournamentProduct(code, TYPE_CUBE, display, code, null, Collections.<String>emptyList(),
                code, liveMax, true, false);
    }

    private static TournamentProduct wattoCube() {
        List<String> packs = new ArrayList<String>();
        for (int i = 0; i < 4; i++)
            packs.add(ProductName.CUBE_DRAFT_PACK_DARK);
        for (int i = 0; i < 4; i++)
            packs.add(ProductName.CUBE_DRAFT_PACK_LIGHT);
        return new TournamentProduct("wattos_cube", TYPE_CUBE, "Watto's Cube",
                SealedLeagueType.WATTOS_CUBE_WITH_OBJECTIVE_PACKS.getFormatCode(), null, packs,
                null, 8, false, true);
    }

    private static List<String> packs(Object... nameAndCount) {
        List<String> packs = new ArrayList<String>();
        for (int i = 0; i < nameAndCount.length; i += 2) {
            String name = (String) nameAndCount[i];
            int count = (Integer) nameAndCount[i + 1];
            for (int n = 0; n < count; n++)
                packs.add(name);
        }
        return packs;
    }

    private static List<String> uniquePackNames(List<String> packs) {
        List<String> unique = new ArrayList<String>();
        for (String pack : packs) {
            if (!unique.contains(pack))
                unique.add(pack);
        }
        return unique;
    }

    public static List<Integer> cubePackChoices() {
        return Arrays.asList(4, 6, 8);
    }
}
