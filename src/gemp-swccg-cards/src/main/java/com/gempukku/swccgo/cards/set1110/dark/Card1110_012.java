package com.gempukku.swccgo.cards.set1110.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.SubstituteDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 10: Fourth Anthology
 * Type: Character
 * Subtype: Imperial
 * Title: Vader
 */
public class Card1110_012 extends AbstractImperial {
    public Card1110_012() {
        super(Side.DARK, 1, 6, 6, 6, 8, Title.Vader, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_10, Rarity.V);
        setVirtualSuffix(true);
        setLore("Sought to extinguish all Jedi. Former student of Obi-Wan Kenobi. Seduced by the dark side of the Force.");
        setGameText("Adds 4 to power of anything he pilots. Jedi are defense value -1 and forfeit -2 at same and related sites. During battle, if you are about to draw a card for weapon or battle destiny, may instead use an Interrupt of destiny < 4 from hand. Immune to attrition < 5.");
        addPersona(Persona.VADER);
        addIcons(Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter jediAtSameOrRelatedSite = Filters.and(Filters.Jedi, Filters.at(Filters.sameOrRelatedSite(self)));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 4));
        modifiers.add(new DefenseValueModifier(self, jediAtSameOrRelatedSite, -1));
        modifiers.add(new ForfeitModifier(self, jediAtSameOrRelatedSite, -2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__VADER_V__USE_INTERRUPT_FOR_DESTINY;
        final Filter interruptFromHand = Filters.and(Filters.Interrupt, Filters.destinyLessThan(4));

        if ((TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                || TriggerConditions.isAboutToDrawWeaponDestiny(game, effectResult, playerId))
                && GameConditions.isInBattle(game, self)
                && GameConditions.canSubstituteDestiny(game)
                && GameConditions.hasInHand(game, playerId, interruptFromHand)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Substitute destiny");
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId, interruptFromHand) {
                        @Override
                        protected void cardSelected(final SwccgGame game, final PhysicalCard selectedCard) {
                            action.appendCost(
                                    new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(selectedCard)) {
                                        @Override
                                        protected void refreshedPrintedDestinyValues() {
                                            final float destinyValue = game.getModifiersQuerying().getDestiny(game.getGameState(), selectedCard);
                                            DestinyType destinyType = game.getGameState().getTopDrawDestinyState().getDrawDestinyEffect().getDestinyType();
                                            action.setActionMsg("Substitute " + GameUtils.getCardLink(selectedCard) + "'s destiny value of " + GuiUtils.formatAsString(destinyValue) + " for " + destinyType.getHumanReadable());
                                            action.appendEffect(
                                                    new PutCardFromHandOnUsedPileEffect(action, playerId, selectedCard, false));
                                            action.appendEffect(
                                                    new SubstituteDestinyEffect(action, destinyValue));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
