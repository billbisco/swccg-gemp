package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * The abstract class providing the common implementation for Missions (AR 2.0).
 */
public abstract class AbstractMission extends AbstractNonLocationPlaysToTable {

    /**
     * Creates a blueprint for a Mission that deploys on table.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     * @param critical true if this is a Critical Mission
     */
    protected AbstractMission(Side side, float destiny, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity, boolean critical) {
        this(side, destiny, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, title, uniqueness, expansionSet, rarity, critical);
    }

    /**
     * Creates a blueprint for a Mission.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption where the Mission deploys (table, or attached to a card/location)
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     * @param critical true if this is a Critical Mission
     */
    protected AbstractMission(Side side, float destiny, PlayCardZoneOption playCardZoneOption, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity, boolean critical) {
        super(side, destiny, playCardZoneOption, 0f, title, uniqueness, expansionSet, rarity);
        setCardCategory(CardCategory.MISSION);
        addCardType(CardType.MISSION);
        addIcon(Icon.MISSION);
        setCardSubtype(critical ? CardSubtype.CRITICAL : CardSubtype.NORMAL);
    }

    @Override
    protected boolean checkPlayRequirements(String playerId, SwccgGame game, PhysicalCard self, DeploymentRestrictionsOption deploymentRestrictionsOption, PlayCardOption playCardOption, ReactActionOption reactActionOption) {
        if (!super.checkPlayRequirements(playerId, game, self, deploymentRestrictionsOption, playCardOption, reactActionOption)) {
            return false;
        }
        if (getCardSubtype() != CardSubtype.CRITICAL) {
            return true;
        }
        if (game.getGameState().getObjectivePlayed(playerId) != null) {
            return false;
        }
        if (game.getGameState().hasPlayedCriticalMission(playerId)) {
            return false;
        }
        return Filters.findFirstFromAllOnTable(game, Filters.and(CardType.MISSION, CardSubtype.CRITICAL, Filters.your(playerId))) == null;
    }

    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY);
    }

    @Override
    protected final boolean isCardTypeAlwaysPlayedForFree() {
        return true;
    }

    @Override
    public final boolean isCardTypeDeployed() {
        return true;
    }
}
