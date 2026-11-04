package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.cards.AbstractRebelRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Character
 * Subtype: Rebel/Republic
 * Title: Bail Organa, Father Of Rebellion
 */
public class Card1272_014 extends AbstractRebelRepublic {
    public Card1272_014() {
        super(Side.LIGHT, 3, 2, 3, 3, 5, "Bail Organa, Father Of Rebellion", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setPolitics(2);
        setLore("Senator. Leader.");
        setGameText("Agenda: rebellion. Your [Coruscant] Political Effects and [Coruscant] senators are lost. Whenever you initiate a battle with a Rebel, may retrieve 1 Force. While in a senate majority, during your control phase, may take one card into hand from Force Pile; reshuffle.");
        addPersona(Persona.BAIL);
        addIcons(Icon.EPISODE_I);
        addKeywords(Keyword.SENATOR, Keyword.LEADER);
        setSpecies(Species.ALDERAANIAN);
    }

    private static Filter yourCoruscantPoliticalEffectsAndSenators(PhysicalCard self) {
        return Filters.and(Filters.your(self), Icon.CORUSCANT, Filters.or(Filters.Political_Effect, Filters.senator));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.REBELLION));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Collection<PhysicalCard> toLose = Filters.filterActive(game, self, yourCoruscantPoliticalEffectsAndSenators(self));
            if (!toLose.isEmpty()) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make " + GameUtils.getAppendedNames(toLose) + " lost");
                action.setActionMsg("Make " + GameUtils.getAppendedNames(toLose) + " lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, toLose));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BAIL_FATHER_OF_REBELLION__RETRIEVE_FORCE;

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, playerId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Rebel, Filters.participatingInBattle))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 1 Force");
            action.setActionMsg("Retrieve 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BAIL_FATHER_OF_REBELLION__TAKE_CARD_FROM_FORCE_PILE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isInSenateMajority(game, self)
                && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Force Pile");
            action.setActionMsg("Take one card into hand from Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromForcePileEffect(action, playerId, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
