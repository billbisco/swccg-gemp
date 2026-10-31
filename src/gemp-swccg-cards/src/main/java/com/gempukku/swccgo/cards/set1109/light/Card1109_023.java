package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployPilotSimultaneouslyToTargetWithoutPresenceOrForceIconsModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployWithoutPresenceOrForceIconsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Set 9
 * Type: Starship
 * Subtype: Starfighter
 * Title: Tydirium
 */
public class Card1109_023 extends AbstractStarfighter {
    public Card1109_023() {
        super(Side.LIGHT, 3, 2, 2, null, 2, 3, 4, Title.Tydirium, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("Stolen Imperial Lambda-class shuttle. Supposedly carried parts and technical crew. Delivered General Solo's crack team of Rebel scouts to the forest moon of Endor.");
        setGameText("May deploy (with a pilot) without presence or Force icons. May add 2 pilots and 6 passengers. May forfeit a scout aboard to cancel all remaining battle damage against you.");
        addIcons(Icon.ENDOR, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.LAMBDA_CLASS_SHUTTLE);
        setPilotCapacity(2);
        setPassengerCapacity(6);
        setAlwaysStolen(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployWithoutPresenceOrForceIconsModifier(self));
        modifiers.add(new MayDeployPilotSimultaneouslyToTargetWithoutPresenceOrForceIconsModifier(self, Filters.any));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.isBattleDamageRemaining(game, playerId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.hasAboard(game, self, Filters.scout)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Forfeit a scout aboard to cancel battle damage");
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose scout aboard to forfeit",
                            Filters.and(Filters.scout, Filters.aboard(self))) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.addAnimationGroup(selectedCard);
                            action.setActionMsg("Forfeit " + GameUtils.getCardLink(selectedCard) + " to cancel remaining battle damage");
                            action.appendCost(
                                    new ForfeitCardFromTableEffect(action, selectedCard));
                            action.appendEffect(
                                    new SatisfyAllBattleDamageEffect(action, playerId));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
