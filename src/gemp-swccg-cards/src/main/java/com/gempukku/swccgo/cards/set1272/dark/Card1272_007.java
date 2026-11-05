package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromUsedPileInLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionLimitedToModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PutCardInCardPileFromOffTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Effect
 * Title: Where Are You Taking Them?
 */
public class Card1272_007 extends AbstractNormalEffect {
    public Card1272_007() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Where Are You Taking Them?", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setLore("'Coruscant?'");
        setGameText("Deploy on table. At non-[Episode I] sites, immunity to attrition of [Coruscant] Jedi is limited to < 5. Your gametext on [Coruscant] systems is canceled. During your control phase, may say 'Coruscant?' and lose 1 Force to place a just-resolved Interrupt in owner's Lost Pile. (Immune to Alter.)");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmunityToAttritionLimitedToModifier(self,
                Filters.and(Filters.Jedi, Icon.CORUSCANT, Filters.at(Filters.and(Filters.site, Filters.not(Icon.EPISODE_I)))), 5));
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.and(Filters.system, Icon.CORUSCANT), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__WHERE_ARE_YOU_TAKING_THEM__PLACE_INTERRUPT_IN_LOST_PILE;

        // Check condition(s)
        if (effectResult.getType() == EffectResult.Type.PUT_IN_CARD_PILE_FROM_OFF_TABLE
                && playerId.equals(self.getOwner())
                && GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)) {
            PutCardInCardPileFromOffTableResult result = (PutCardInCardPileFromOffTableResult) effectResult;
            final PhysicalCard interrupt = result.getCard();
            if (result.isPlayedInterrupt()
                    && result.getCardPile() == Zone.USED_PILE
                    && interrupt != null
                    && GameUtils.getZoneFromZoneTop(interrupt.getZone()) == Zone.USED_PILE) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Say 'Coruscant?' to place " + GameUtils.getFullName(interrupt) + " in Lost Pile");
                action.setActionMsg("Say 'Coruscant?' and place " + GameUtils.getCardLink(interrupt) + " in owner's Lost Pile");
                // Pay cost(s)
                action.appendCost(
                        new LoseForceEffect(action, playerId, 1, true));
                // Perform result(s)
                action.appendEffect(
                        new PutCardFromUsedPileInLostPileEffect(action, playerId, interrupt));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
