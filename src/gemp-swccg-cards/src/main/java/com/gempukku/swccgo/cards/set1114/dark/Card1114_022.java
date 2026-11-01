package com.gempukku.swccgo.cards.set1114.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.StackCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 14: Scum and Villainy
 * Type: Character
 * Subtype: Imperial
 * Title: Commander Nemet
 */
public class Card1114_022 extends AbstractImperial {
    public Card1114_022() {
        super(Side.DARK, 2, 2, 1, 2, 3, "Commander Nemet", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_14, Rarity.V);
        setVirtualSuffix(true);
        setLore("ogistics officer for the Avenger, member of the Line Branch of the Imperial Navy. Relays important scanner information to Captain Needa. Fiercely competitive.");
        setGameText("Adds 1 to power of anything he pilots. While piloting at a battleground system, your Force drains here may not be canceled. While piloting at a system with a 'probe' card, during your deploy phase (or if just forfeited), may be placed as a 'probe' card at any system.");
        addIcons(Icon.PILOT, Icon.DAGOBAH);
        addKeywords(Keyword.COMMANDER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.here(self),
                new AndCondition(new PilotingCondition(self), new com.gempukku.swccgo.cards.conditions.AtCondition(self, Filters.and(Filters.system, Filters.battleground))),
                opponent, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__NEMET_V__PLACE_AS_PROBE;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.isPiloting(game, self, Filters.any)
                && GameConditions.isAtLocation(game, self, Filters.and(Filters.system, Filters.hasStacked(Filters.probeCard)))
                && GameConditions.canSpot(game, self, Filters.system)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place as a 'probe' card");
            appendPlaceAsProbe(action, playerId, game, self);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__NEMET_V__PLACE_AS_PROBE;
        if (TriggerConditions.justForfeited(game, effectResult, playerId, self)
                && GameConditions.canSpot(game, self, Filters.system)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place as a 'probe' card");
            appendPlaceAsProbe(action, playerId, game, self);
            return Collections.singletonList(action);
        }
        return null;
    }

    private void appendPlaceAsProbe(final Action action, final String playerId, final SwccgGame game, final PhysicalCard self) {
        action.appendTargeting(
                new ChooseCardOnTableEffect(action, playerId, "Choose system to 'probe'", Filters.system) {
                    @Override
                    protected void cardSelected(final PhysicalCard system) {
                        action.addAnimationGroup(system);
                        action.appendEffect(
                                new StackCardFromTableEffect(action, self, system, true));
                        action.appendEffect(
                                new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame swccgGame) {
                                        self.setProbeCard(true);
                                    }
                                });
                    }
                }
        );
    }
}
