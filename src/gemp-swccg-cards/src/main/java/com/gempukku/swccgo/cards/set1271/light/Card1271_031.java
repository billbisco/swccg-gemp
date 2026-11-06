package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.InsteadOfForceDrainingEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.1
 * Type: Character
 * Subtype: Rebel
 * Title: Biggs, Rogue Legend
 */
public class Card1271_031 extends AbstractRebel {
    public Card1271_031() {
        super(Side.LIGHT, 2, 2, 2, 2, 5, "Biggs, Rogue Legend", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setGameText("[Pilot] 2, 3: Red 3. While piloting Red 3, it is maneuver +1 and he draws one battle destiny if not able to otherwise. If at opponent's system, instead of Force draining here, may take any one card into hand from Force Pile; reshuffle.");
        setLore("Piloted Red 3 at Battle of Yavin. Childhood friend of Luke. Led mutiny on Rand Ecliptic and theft of Ecliptic from shipyards on Bestine. Ecliptic Evaders emblem on helmet.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addPersona(Persona.BIGGS);
        addKeywords(Keyword.RED_SQUADRON, Keyword.ROGUE_SQUADRON);
        setMatchingStarshipFilter(Filters.Red_3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition whilePilotingRed3 = new PilotingCondition(self, Filters.Red_3);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.Red_3)));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), whilePilotingRed3, 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, whilePilotingRed3, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BIGGS_ROGUE_LEGEND__TAKE_FORCE_PILE;

        // Check condition(s)
        if (GameConditions.isAtLocation(game, self, Filters.and(Filters.opponents(self), Filters.system))) {
            GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, self);
            if (GameConditions.canInsteadOfForceDrainingAtLocation(game, playerId, location)
                    && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, gameTextActionId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take card into hand from Force Pile");
                action.setActionMsg("Take a card into hand from Force Pile");
                // Perform result(s)
                action.appendEffect(
                        new InsteadOfForceDrainingEffect(action, location,
                                new TakeCardIntoHandFromForcePileEffect(action, playerId, true)));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
