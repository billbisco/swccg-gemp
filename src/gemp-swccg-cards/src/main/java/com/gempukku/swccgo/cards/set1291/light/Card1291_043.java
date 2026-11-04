package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractNewRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Character
 * Subtype: New Republic
 * Title: General Bel Iblis
 */
public class Card1291_043 extends AbstractNewRepublic {
    private static final Filter NON_EPISODE_I_CORVETTE_OR_STOLEN_STARSHIP = Filters.or(
            Filters.and(Filters.corvette, Filters.not(Icon.EPISODE_I)),
            Filters.and(Filters.starship, Filters.stolen));

    public Card1291_043() {
        super(Side.LIGHT, 1, 4, 2, 4, 7, "General Bel Iblis", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLore("Insurgent leader. Corellian.");
        setGameText("Adds 3 to power of anything he pilots. While piloting a non-[Episode I] corvette or a stolen starship, adds one battle destiny and it is immune to attrition < 5. 'Exiled' (placed out of play) if at Corellia. Once per game, may steal a Dreadnaught from opponent's Lost Pile into hand.");
        addIcons(Icon.PILOT);
        addKeywords(Keyword.GENERAL, Keyword.LEADER);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, new PilotingCondition(self, NON_EPISODE_I_CORVETTE_OR_STOLEN_STARSHIP), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(NON_EPISODE_I_CORVETTE_OR_STOLEN_STARSHIP, Filters.hasPiloting(self)), 5));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isAtLocation(game, self, Filters.Corellia_location)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("'Exile' (place out of play)");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__GENERAL_BEL_IBLIS__STEAL_DREADNAUGHT;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canStealCardsFromLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Steal a Dreadnaught from opponent's Lost Pile");
            action.setActionMsg("Steal a Dreadnaught into hand from opponent's Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new StealCardIntoHandFromLostPileEffect(action, playerId, Filters.Dreadnaught));
            return Collections.singletonList(action);
        }
        return null;
    }
}
