package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Starship
 * Subtype: Starfighter
 * Title: Azure Angel
 */
public class Card1291_070 extends AbstractStarfighter {
    public Card1291_070() {
        super(Side.LIGHT, 2, 3, 3, null, 4, 6, 6, Title.Azure_Angel, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setGameText("May add 1 pilot and 1 astromech. Once per game, may [download] an astromech aboard (for free). While Anakin piloting, your Interrupts targeting this starship may not be canceled and immune to attrition < 5.");
        addIcons(Icon.NAV_COMPUTER, Icon.CLONE_ARMY, Icon.SCOMP_LINK, Icon.EPISODE_I, Icon.REPUBLIC);
        addModelType(ModelType.MODIFIED_DELTA_7_INTERCEPTOR);
        setPilotCapacity(1);
        setAstromechCapacity(1);
        setMatchingPilotFilter(Filters.Anakin);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__AZURE_ANGEL_V__DEPLOY_ASTROMECH;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy astromech aboard from Reserve Deck");
            action.setActionMsg("Deploy an astromech aboard from Reserve Deck (for free)");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new DeployCardAboardFromReserveDeckEffect(action, Filters.astromech_droid, Filters.sameCardId(self), true, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition anakinPiloting = new HasPilotingCondition(self, Filters.Anakin);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeCanceledModifier(self, Filters.and(Filters.your(self), Filters.Interrupt, Filters.cardBeingPlayedTargeting(self, self)), anakinPiloting));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, anakinPiloting, 5));
        return modifiers;
    }
}
