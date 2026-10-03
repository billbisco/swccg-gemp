package com.gempukku.swccgo.ai.models;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.JediTestStatus;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.ForceDrainState;
import com.gempukku.swccgo.game.state.GameState;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Grounded action features after the choice-table block.
 *
 * <p>Card names are not slots. Identity stays in the text-hash and blueprint-hash
 * buckets. Type, subtype, and category slots are the Java enums, in enum order.
 * Numeric slots are the card's own field divided by a fixed scale so the value
 * sits near {@code [0, 1]}. The scale is not a score. New weights start at 0.
 *
 * <p>A fact that is identical on every candidate cannot change greedy argmax and
 * its softmax gradient is 0. Battle, drain, and out-of-play numbers are therefore
 * written only on a candidate the fact is about (battle/weapon/fire text or a
 * weapon or character card; drain text; out-of-play text or subtype).
 *
 * <p>Not present, so not a slot: used and lost pile sizes (already in the packed
 * vector), seen-destiny count and the high-destiny remaining estimate (packed),
 * a podracer's race-location name (no such field; only "at a system"), remaining
 * Jedi Test destiny (the state enum is not a count), a numeric immunity-to-attrition
 * value (blueprint exposes only the boolean), the full model-type and keyword
 * enums, and an "action is legal" flag (every enumerated candidate is already legal).
 */
public final class LinearActionFeatures {

    /** First grounded slot. Choice-table features occupy the indices just before this. */
    public static final int GROUNDED_START = LinearPolicyAi.AF_CHOICE + LinearPolicyAi.AF_CHOICE_COUNT;

    public static final CardType[] TYPES = CardType.values();
    public static final CardSubtype[] SUBTYPES = CardSubtype.values();
    public static final CardCategory[] CATEGORIES = CardCategory.values();
    public static final Side[] SIDES = Side.values();

    public static final String[] FACTS = {
            "stat:destiny",
            "stat:alternateDestiny",
            "stat:deployCost",
            "stat:power",
            "stat:ability",
            "stat:politics",
            "stat:forfeit",
            "stat:armor",
            "stat:maneuver",
            "stat:landspeed",
            "stat:ferocity",
            "stat:hyperspeed",
            "stat:specialDefense",
            "stat:pilotCapacity",
            "stat:passengerCapacity",
            "stat:astromechCapacity",
            "stat:vehicleCapacity",
            "stat:capitalCapacity",
            "stat:starfighterCapacity",
            "stat:parsec",
            "uniq:unique",
            "uniq:restricted",
            "uniq:diamond",
            "fact:hasPersona",
            "fact:personaCount",
            "fact:matchingShip",
            "fact:presenceIcon",
            "fact:presenceIconCount",
            "fact:immuneAttrition",
            "fact:immuneOpponentObjective",
            "fact:mayNotBeCanceled",
            "fact:political",
            "fact:weaponNeedsPresence",
            "fact:jediTest1",
            "fact:jediTest2",
            "fact:jediTest3",
            "fact:jediTest4",
            "fact:jediTest5",
            "fact:jediTest6",
            "fact:hasSpecies",
            "fact:modelTypeCount",
            "fact:permanentWeapon",
            "fact:bearerIsCharacter",
            "fact:atSystemLocation",
            "fact:hasSystemName",
            "fact:deploysOrbiting",
            "fact:combo",
            "fact:alwaysStolen",
            "fact:movesLikeCharacter",
            "fact:movesLikeStarfighter",
            "fact:deploysLikeStarfighter",
            "fact:deployBothPiles",
            "fact:notDeckLimit",
            "fact:mayNotReserve",
            "fact:vehicleSlotOk",
            "fact:personaOnlyOnTable",
            "fact:doubleSidedFront",
            "fact:jediTestNotCompleted",
            "fact:jediTestAttempting",
            "fact:jediTestCompleted",
            "flag:textOncePerTurn",
            "flag:textOncePerBattle",
            "flag:textMatching",
            "flag:textImmune",
            "sit:duringBattle",
            "sit:myBattlePower",
            "sit:oppBattlePower",
            "sit:myAttrition",
            "sit:oppAttrition",
            "sit:myBattleCount",
            "sit:oppBattleCount",
            "sit:iInitiatedBattle",
            "sit:damageSegment",
            "sit:bombingRun",
            "sit:besieged",
            "sit:localTrouble",
            "sit:myBattleDamage",
            "sit:oppBattleDamage",
            "sit:duringDrain",
            "sit:drainTotal",
            "sit:drainRemaining",
            "sit:iControlDrainLocation",
            "sit:myOutOfPlay",
            "sit:oppOutOfPlay"
    };

    public static final int TYPE_AT = GROUNDED_START;
    public static final int SUBTYPE_AT = TYPE_AT + TYPES.length;
    public static final int CATEGORY_AT = SUBTYPE_AT + SUBTYPES.length;
    public static final int SIDE_AT = CATEGORY_AT + CATEGORIES.length;
    public static final int FACT_AT = SIDE_AT + SIDES.length;
    public static final int GROUNDED_DIM = FACT_AT + FACTS.length;

    private static final Keyword[] JEDI_TESTS = {
            Keyword.JEDI_TEST_1, Keyword.JEDI_TEST_2, Keyword.JEDI_TEST_3,
            Keyword.JEDI_TEST_4, Keyword.JEDI_TEST_5, Keyword.JEDI_TEST_6
    };

    private LinearActionFeatures() {
    }

    public static int groundedDim() {
        return GROUNDED_DIM;
    }

    /** Fill type/subtype/context/situation slots. Leaves 0 when the card or state is missing. */
    public static void fill(float[] feat, String text, String blueprintId, String cardId,
                            GameState gameState, String playerId) {
        if (feat == null || feat.length < GROUNDED_DIM) {
            return;
        }
        PhysicalCard card = findCard(gameState, blueprintId, cardId);
        SwccgCardBlueprint bp = card != null ? safeBlueprint(card) : null;
        String kindText = text == null ? "" : text.toLowerCase(Locale.ROOT);
        if (bp != null) {
            fillPrinted(feat, bp, card, kindText);
        }
        fillSituation(feat, kindText, bp, gameState, playerId);
    }

    /**
     * Non-zero grounded slots as {@code idx:value} pairs. Text features are not included.
     */
    public static String nonzeroGrounded(float[] feat) {
        if (feat == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int end = Math.min(feat.length, GROUNDED_DIM);
        for (int i = GROUNDED_START; i < end; i++) {
            float v = feat[i];
            if (v == 0f) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(i).append(':').append(trim(v));
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public static void annotateItems(Map<String, Object> options, GameState gameState, String playerId) {
        if (options == null || gameState == null) {
            return;
        }
        Object raw = options.get("items");
        if (!(raw instanceof List<?> items)) {
            return;
        }
        for (Object row : items) {
            if (!(row instanceof Map<?, ?> map)) {
                continue;
            }
            Map<String, String> opt = (Map<String, String>) map;
            String text = opt.getOrDefault("text", "");
            String bp = opt.getOrDefault("blueprintId", "");
            String cardId = opt.getOrDefault("cardId", "");
            float[] feat = LinearPolicyAi.actionFeatures(text, bp, cardId, false, 0f, 0f, gameState, playerId);
            String nz = nonzeroGrounded(feat);
            if (!nz.isEmpty()) {
                opt.put("nz", nz);
            }
        }
    }

    private static void fillPrinted(float[] feat, SwccgCardBlueprint bp, PhysicalCard card, String kindText) {
        try {
            Set<CardType> types = bp.getCardTypes();
            if (types != null) {
                for (int i = 0; i < TYPES.length; i++) {
                    if (types.contains(TYPES[i])) {
                        feat[TYPE_AT + i] = 1f;
                    }
                }
            }
        } catch (RuntimeException ignored) {
            // printed type missing
        }
        try {
            CardSubtype subtype = bp.getCardSubtype();
            for (int i = 0; i < SUBTYPES.length; i++) {
                if (subtype == SUBTYPES[i]) {
                    feat[SUBTYPE_AT + i] = 1f;
                }
            }
        } catch (RuntimeException ignored) {
            // subtype missing
        }
        try {
            CardCategory category = bp.getCardCategory();
            for (int i = 0; i < CATEGORIES.length; i++) {
                if (category == CATEGORIES[i]) {
                    feat[CATEGORY_AT + i] = 1f;
                }
            }
        } catch (RuntimeException ignored) {
            // category missing
        }
        try {
            Side side = bp.getSide();
            for (int i = 0; i < SIDES.length; i++) {
                if (side == SIDES[i]) {
                    feat[SIDE_AT + i] = 1f;
                }
            }
        } catch (RuntimeException ignored) {
            // side missing
        }

        set(feat, "stat:destiny", unit(callFloat(bp::getDestiny), 7f));
        set(feat, "stat:alternateDestiny", unit(callFloat(bp::getAlternateDestiny), 7f));
        set(feat, "stat:deployCost", unit(callFloat(bp::getDeployCost), 10f));
        if (flag(bp::hasPowerAttribute)) {
            set(feat, "stat:power", unit(callFloat(bp::getPower), 10f));
        }
        if (flag(bp::hasAbilityAttribute)) {
            set(feat, "stat:ability", unit(callFloat(bp::getAbility), 10f));
        }
        if (flag(bp::hasPoliticsAttribute)) {
            set(feat, "stat:politics", unit(callFloatPrim(bp::getPolitics), 10f));
            set(feat, "fact:political", 1f);
        }
        if (flag(bp::hasForfeitAttribute)) {
            set(feat, "stat:forfeit", unit(callFloat(bp::getForfeit), 10f));
        }
        if (flag(bp::hasArmorAttribute)) {
            set(feat, "stat:armor", unit(callFloat(bp::getArmor), 10f));
        }
        if (flag(bp::hasManeuverAttribute)) {
            set(feat, "stat:maneuver", unit(callFloat(bp::getManeuver), 10f));
        }
        if (flag(bp::hasLandspeedAttribute)) {
            set(feat, "stat:landspeed", unit(callFloat(bp::getLandspeed), 10f));
        }
        if (flag(bp::hasFerocityAttribute)) {
            set(feat, "stat:ferocity", unit(callFloat(bp::getFerocity), 10f));
        }
        if (flag(bp::hasHyperspeedAttribute)) {
            set(feat, "stat:hyperspeed", unit(callFloat(bp::getHyperspeed), 10f));
        }
        if (flag(bp::hasSpecialDefenseValueAttribute)) {
            set(feat, "stat:specialDefense", unit(callFloatPrim(bp::getSpecialDefenseValue), 10f));
        }
        set(feat, "stat:pilotCapacity", unitInt(callInt(bp::getPilotCapacity), 8f));
        set(feat, "stat:passengerCapacity", unitInt(callInt(bp::getPassengerCapacity), 8f));
        set(feat, "stat:astromechCapacity", unitInt(callInt(bp::getAstromechCapacity), 4f));
        set(feat, "stat:vehicleCapacity", unitInt(callInt(bp::getVehicleCapacity), 8f));
        set(feat, "stat:capitalCapacity", unitInt(callInt(bp::getCapitalStarshipCapacity), 6f));
        set(feat, "stat:starfighterCapacity", unitInt(callInt(bp::getStarfighterOrTIECapacity), 8f));
        set(feat, "stat:parsec", unitInt(callInt(bp::getParsec), 20f));

        try {
            Uniqueness uniqueness = bp.getUniqueness();
            if (uniqueness != null) {
                String name = uniqueness.name();
                if ("UNIQUE".equals(name)) {
                    set(feat, "uniq:unique", 1f);
                } else if (name.startsWith("RESTRICTED")) {
                    set(feat, "uniq:restricted", 1f);
                } else if (name.startsWith("DIAMOND")) {
                    set(feat, "uniq:diamond", 1f);
                }
            }
        } catch (RuntimeException ignored) {
            // uniqueness missing
        }

        try {
            Set<?> personas = bp.getPersonas();
            int n = personas == null ? 0 : personas.size();
            if (n > 0) {
                set(feat, "fact:hasPersona", 1f);
                set(feat, "fact:personaCount", unitInt(n, 3f));
            }
        } catch (RuntimeException ignored) {
            // personas missing
        }
        if (callObj(bp::getRelatedStarshipOrVehiclePersona) != null) {
            set(feat, "fact:matchingShip", 1f);
        }
        if (flag(() -> bp.hasIcon(Icon.PRESENCE))) {
            set(feat, "fact:presenceIcon", 1f);
            set(feat, "fact:presenceIconCount", unitInt(callInt(() -> bp.getIconCount(Icon.PRESENCE)), 3f));
        }
        if (flag(bp::hasImmunityToAttritionAttribute)) {
            set(feat, "fact:immuneAttrition", 1f);
        }
        if (flag(bp::isImmuneToOpponentsObjective)) {
            set(feat, "fact:immuneOpponentObjective", 1f);
        }
        if (flag(bp::isCardTypeMayNotBeCanceled)) {
            set(feat, "fact:mayNotBeCanceled", 1f);
        }
        try {
            if (bp.getCardSubtype() == CardSubtype.POLITICAL) {
                set(feat, "fact:political", 1f);
            }
        } catch (RuntimeException ignored) {
            // subtype already handled
        }
        if (flag(bp::isFiredByCharacterPresentOrHere)) {
            set(feat, "fact:weaponNeedsPresence", 1f);
        }
        for (int i = 0; i < JEDI_TESTS.length; i++) {
            Keyword keyword = JEDI_TESTS[i];
            if (flag(() -> bp.hasKeyword(keyword))) {
                set(feat, "fact:jediTest" + (i + 1), 1f);
            }
        }
        if (flag(bp::hasSpeciesAttribute)) {
            set(feat, "fact:hasSpecies", 1f);
        }
        try {
            List<?> models = bp.getModelTypes();
            if (models != null && !models.isEmpty()) {
                set(feat, "fact:modelTypeCount", unitInt(models.size(), 3f));
            }
        } catch (RuntimeException ignored) {
            // model types missing
        }
        if (card != null) {
            try {
                if (bp.getPermanentWeapon(card) != null) {
                    set(feat, "fact:permanentWeapon", 1f);
                }
            } catch (RuntimeException ignored) {
                // permanent weapon not available
            }
            try {
                PhysicalCard bearer = card.getAttachedTo();
                if (bearer != null && bearer.getBlueprint() != null
                        && bearer.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                    set(feat, "fact:bearerIsCharacter", 1f);
                }
            } catch (RuntimeException ignored) {
                // bearer missing
            }
            try {
                PhysicalCard at = card.getAtLocation();
                if (at != null && at.getBlueprint() != null
                        && at.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM) {
                    set(feat, "fact:atSystemLocation", 1f);
                }
            } catch (RuntimeException ignored) {
                // location missing
            }
            try {
                if (bp.getCardCategory() == CardCategory.JEDI_TEST && card.getJediTestStatus() != null) {
                    JediTestStatus status = card.getJediTestStatus();
                    if (status == JediTestStatus.NOT_COMPLETED) {
                        set(feat, "fact:jediTestNotCompleted", 1f);
                    } else if (status == JediTestStatus.ATTEMPTING) {
                        set(feat, "fact:jediTestAttempting", 1f);
                    } else if (status == JediTestStatus.COMPLETED) {
                        set(feat, "fact:jediTestCompleted", 1f);
                    }
                }
            } catch (RuntimeException ignored) {
                // jedi test status missing
            }
        }
        String system = callString(bp::getSystemName);
        if (system != null && !system.isEmpty()) {
            set(feat, "fact:hasSystemName", 1f);
        }
        String orbit = callString(bp::getDeploysOrbitingSystem);
        if (orbit != null && !orbit.isEmpty()) {
            set(feat, "fact:deploysOrbiting", 1f);
        }
        if (flag(bp::isComboCard)) {
            set(feat, "fact:combo", 1f);
        }
        if (flag(bp::isAlwaysStolen)) {
            set(feat, "fact:alwaysStolen", 1f);
        }
        if (flag(bp::isMovesLikeCharacter)) {
            set(feat, "fact:movesLikeCharacter", 1f);
        }
        if (flag(bp::isMovesLikeStarfighter)) {
            set(feat, "fact:movesLikeStarfighter", 1f);
        }
        if (flag(bp::isDeploysLikeStarfighter)) {
            set(feat, "fact:deploysLikeStarfighter", 1f);
        }
        if (flag(bp::isDeployUsingBothForcePiles)) {
            set(feat, "fact:deployBothPiles", 1f);
        }
        if (flag(bp::isDoesNotCountTowardDeckLimit)) {
            set(feat, "fact:notDeckLimit", 1f);
        }
        if (flag(bp::isMayNotBePlacedInReserveDeck)) {
            set(feat, "fact:mayNotReserve", 1f);
        }
        if (flag(bp::isVehicleSlotOfStarshipCompatible)) {
            set(feat, "fact:vehicleSlotOk", 1f);
        }
        if (flag(bp::hasCharacterPersonaOnlyWhileOnTable)) {
            set(feat, "fact:personaOnlyOnTable", 1f);
        }
        if (flag(bp::isFrontOfDoubleSidedCard)) {
            set(feat, "fact:doubleSidedFront", 1f);
        }
        String gameText = callString(bp::getGameText);
        if (gameText != null) {
            String lower = gameText.toLowerCase(Locale.ROOT);
            if (lower.contains("once per turn")) {
                set(feat, "flag:textOncePerTurn", 1f);
            }
            if (lower.contains("once per battle")) {
                set(feat, "flag:textOncePerBattle", 1f);
            }
            if (lower.contains("matching")) {
                set(feat, "flag:textMatching", 1f);
            }
            if (lower.contains("immune")) {
                set(feat, "flag:textImmune", 1f);
            }
        }
    }

    private static void fillSituation(float[] feat, String kindText, SwccgCardBlueprint bp,
                                      GameState gameState, String playerId) {
        if (gameState == null || playerId == null || playerId.isBlank()) {
            return;
        }
        boolean battleAbout = kindText.contains("battle") || kindText.contains("weapon") || kindText.contains("fire")
                || hasType(bp, CardType.WEAPON) || categoryIs(bp, CardCategory.CHARACTER)
                || categoryIs(bp, CardCategory.WEAPON);
        boolean drainAbout = kindText.contains("drain");
        boolean oopAbout = kindText.contains("out of play") || subtypeIs(bp, CardSubtype.OUT_OF_PLAY);
        String opp = opponent(gameState, playerId);
        SwccgGame game = safeGame(gameState);
        if (battleAbout && game != null) {
            try {
                if (gameState.isDuringBattle()) {
                    set(feat, "sit:duringBattle", 1f);
                    BattleState battle = gameState.getBattleState();
                    if (battle != null) {
                        set(feat, "sit:myBattlePower", unit(battle.getTotalPower(game, playerId), 40f));
                        if (opp != null) {
                            set(feat, "sit:oppBattlePower", unit(battle.getTotalPower(game, opp), 40f));
                            set(feat, "sit:myAttrition", unit(safeAttrition(battle, game, playerId), 20f));
                            set(feat, "sit:oppAttrition", unit(safeAttrition(battle, game, opp), 20f));
                            set(feat, "sit:myBattleDamage", unit(safeDamage(battle, game, playerId), 20f));
                            set(feat, "sit:oppBattleDamage", unit(safeDamage(battle, game, opp), 20f));
                            set(feat, "sit:oppBattleCount", unitInt(size(battle.getCardsParticipating(opp)), 12f));
                        }
                        set(feat, "sit:myBattleCount", unitInt(size(battle.getCardsParticipating(playerId)), 12f));
                        if (battle.isBombingRun()) {
                            set(feat, "sit:bombingRun", 1f);
                        }
                        if (battle.isBesieged()) {
                            set(feat, "sit:besieged", 1f);
                        }
                        if (battle.isLocalTrouble()) {
                            set(feat, "sit:localTrouble", 1f);
                        }
                    }
                    if (gameState.isDuringBattleInitiatedBy(playerId)) {
                        set(feat, "sit:iInitiatedBattle", 1f);
                    }
                    if (gameState.isDuringDamageSegmentOfBattle()) {
                        set(feat, "sit:damageSegment", 1f);
                    }
                }
            } catch (RuntimeException ignored) {
                // battle state not readable
            }
        }
        if (drainAbout) {
            try {
                if (gameState.isDuringForceDrain()) {
                    set(feat, "sit:duringDrain", 1f);
                    ForceDrainState drain = gameState.getForceDrainState();
                    if (drain != null) {
                        set(feat, "sit:drainTotal", unitInt(drain.getForceTotal(), 10f));
                        set(feat, "sit:drainRemaining", unitInt(drain.getForceRemaining(), 10f));
                        if (game != null && drain.getLocation() != null
                                && GameConditions.controls(game, playerId, drain.getLocation())) {
                            set(feat, "sit:iControlDrainLocation", 1f);
                        }
                    }
                }
            } catch (RuntimeException ignored) {
                // drain state not readable
            }
        }
        if (oopAbout) {
            try {
                set(feat, "sit:myOutOfPlay", unitInt(size(gameState.getOutOfPlayPile(playerId)), 40f));
                if (opp != null) {
                    set(feat, "sit:oppOutOfPlay", unitInt(size(gameState.getOutOfPlayPile(opp)), 40f));
                }
            } catch (RuntimeException ignored) {
                // out-of-play pile not readable
            }
        }
    }

    private static PhysicalCard findCard(GameState gameState, String blueprintId, String cardId) {
        if (gameState == null) {
            return null;
        }
        try {
            if (cardId != null && !cardId.isEmpty()) {
                Integer id = Integer.valueOf(cardId.trim());
                PhysicalCard byId = gameState.findCardById(id);
                if (byId != null) {
                    return byId;
                }
            }
        } catch (RuntimeException ignored) {
            // not a card id
        }
        if (blueprintId == null || blueprintId.isEmpty()) {
            return null;
        }
        String[] players = {gameState.getDarkPlayer(), gameState.getLightPlayer()};
        Zone[] zones = {
                Zone.RESERVE_DECK, Zone.USED_PILE, Zone.LOST_PILE, Zone.FORCE_PILE, Zone.OUTSIDE_OF_DECK
        };
        for (String player : players) {
            if (player == null) {
                continue;
            }
            PhysicalCard inHand = scan(safeHand(gameState, player), blueprintId);
            if (inHand != null) {
                return inHand;
            }
            PhysicalCard oop = scan(safeList(() -> gameState.getOutOfPlayPile(player)), blueprintId);
            if (oop != null) {
                return oop;
            }
            for (Zone zone : zones) {
                PhysicalCard found = scan(safeList(() -> gameState.getCardPile(player, zone)), blueprintId);
                if (found != null) {
                    return found;
                }
            }
        }
        PhysicalCard inPlay = scan(safeList(gameState::getCardsInPlay), blueprintId);
        if (inPlay != null) {
            return inPlay;
        }
        PhysicalCard stacked = scan(gameState.getAllStackedCards(), blueprintId);
        if (stacked != null) {
            return stacked;
        }
        return scan(gameState.getAllOutOfPlayCards(), blueprintId);
    }

    private static List<PhysicalCard> safeHand(GameState gameState, String player) {
        try {
            return gameState.getHand(player);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static List<PhysicalCard> safeList(java.util.function.Supplier<List<PhysicalCard>> call) {
        try {
            return call.get();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static PhysicalCard scan(List<PhysicalCard> cards, String blueprintId) {
        if (cards == null) {
            return null;
        }
        for (PhysicalCard card : cards) {
            if (card == null) {
                continue;
            }
            try {
                if (blueprintId.equals(card.getBlueprintId(true))) {
                    return card;
                }
            } catch (RuntimeException ignored) {
                // skip this card
            }
        }
        return null;
    }

    private static SwccgCardBlueprint safeBlueprint(PhysicalCard card) {
        try {
            return card.getBlueprint();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static SwccgGame safeGame(GameState gameState) {
        try {
            return gameState.getGame();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static String opponent(GameState gameState, String playerId) {
        try {
            String dark = gameState.getDarkPlayer();
            String light = gameState.getLightPlayer();
            if (playerId.equals(dark)) {
                return light;
            }
            if (playerId.equals(light)) {
                return dark;
            }
        } catch (RuntimeException ignored) {
            return null;
        }
        return null;
    }

    private static boolean hasType(SwccgCardBlueprint bp, CardType type) {
        if (bp == null) {
            return false;
        }
        try {
            return bp.isCardType(type);
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private static boolean categoryIs(SwccgCardBlueprint bp, CardCategory category) {
        if (bp == null) {
            return false;
        }
        try {
            return bp.getCardCategory() == category;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private static boolean subtypeIs(SwccgCardBlueprint bp, CardSubtype subtype) {
        if (bp == null) {
            return false;
        }
        try {
            return bp.getCardSubtype() == subtype;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private static float safeAttrition(BattleState battle, SwccgGame game, String player) {
        try {
            if (!battle.hasAttritionTotal(player)) {
                return 0f;
            }
            return battle.getAttritionTotal(game, player);
        } catch (RuntimeException ex) {
            return 0f;
        }
    }

    private static float safeDamage(BattleState battle, SwccgGame game, String player) {
        try {
            return battle.getBattleDamageTotal(game, player);
        } catch (RuntimeException ex) {
            return 0f;
        }
    }

    private static int size(Collection<?> values) {
        return values == null ? 0 : values.size();
    }

    private static void set(float[] feat, String name, float value) {
        if (value == 0f) {
            return;
        }
        int idx = factIndex(name);
        if (idx >= 0 && idx < feat.length) {
            feat[idx] = value;
        }
    }

    public static int factIndex(String name) {
        for (int i = 0; i < FACTS.length; i++) {
            if (FACTS[i].equals(name)) {
                return FACT_AT + i;
            }
        }
        return -1;
    }

    private static float unit(Float value, float div) {
        if (value == null || div <= 0f) {
            return 0f;
        }
        return clamp(value / div);
    }

    private static float unit(float value, float div) {
        if (div <= 0f) {
            return 0f;
        }
        return clamp(value / div);
    }

    private static float unitInt(int value, float div) {
        if (value <= 0 || div <= 0f) {
            return 0f;
        }
        return clamp(value / div);
    }

    private static float clamp(float value) {
        if (value < 0f) {
            return 0f;
        }
        if (value > 1f) {
            return 1f;
        }
        return value;
    }

    private static String trim(float value) {
        String text = String.format(Locale.ROOT, "%.4f", value);
        if (text.indexOf('.') >= 0) {
            while (text.endsWith("0")) {
                text = text.substring(0, text.length() - 1);
            }
            if (text.endsWith(".")) {
                text = text.substring(0, text.length() - 1);
            }
        }
        return text;
    }

    @FunctionalInterface
    private interface FloatCall {
        Float get();
    }

    @FunctionalInterface
    private interface PrimCall {
        float get();
    }

    @FunctionalInterface
    private interface IntCall {
        int get();
    }

    @FunctionalInterface
    private interface BoolCall {
        boolean get();
    }

    @FunctionalInterface
    private interface ObjCall {
        Object get();
    }

    @FunctionalInterface
    private interface StringCall {
        String get();
    }

    private static Float callFloat(FloatCall call) {
        try {
            return call.get();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static float callFloatPrim(PrimCall call) {
        try {
            return call.get();
        } catch (RuntimeException ex) {
            return 0f;
        }
    }

    private static int callInt(IntCall call) {
        try {
            return call.get();
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    private static boolean flag(BoolCall call) {
        try {
            return call.get();
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private static Object callObj(ObjCall call) {
        try {
            return call.get();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static String callString(StringCall call) {
        try {
            return call.get();
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
