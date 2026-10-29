package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeUsedToSatisfyAttritionModifier;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Reflections Gold
 * Type: Effect
 * Title: Crack Shot & Quick Draw
 */
public class Card15_030 extends AbstractNormalEffect {
    public Card15_030() {
        super(Side.LIGHT, 2, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Crack Shot & Quick Draw", Uniqueness.UNRESTRICTED, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("");
        addComboCardTitles("Crack Shot", "Quick Draw");
        addImmuneToCardTitle(Title.Alter);
        setGameText("Deploy on table. Any card which is 'hit' may not be used to satisfy attrition. Also, once during your draw phase, you may use 2 Force to deploy a blaster from your Lost Pile (for free). (Immune to Alter.)");
        addIcons(Icon.REFLECTIONS_GOLD);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.QUICK_DRAW__DOWNLOAD_BLASTER_FROM_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DRAW)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, true)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy blaster from Lost Pile");
            action.setActionMsg("Deploy a blaster from Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromLostPileEffect(action, Filters.blaster, true, false));
            return Collections.singletonList(action);
        }
        return null;
    }
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new MayNotBeUsedToSatisfyAttritionModifier(self, Filters.hit));
    }
}
