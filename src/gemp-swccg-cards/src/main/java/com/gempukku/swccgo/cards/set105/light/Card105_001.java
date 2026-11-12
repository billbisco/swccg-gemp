package com.gempukku.swccgo.cards.set105.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: First Anthology
 * Type: Character
 * Subtype: Rebel
 * Title: Commander Wedge Antilles
 * <p>
 * Text as printed on the white-border First Anthology preview card (May 1997), which differs from the later Special Edition printing (Bill 2026-11-12; wiki: First Anthology).
 */
public class Card105_001 extends AbstractRebel {
    public Card105_001() {
        super(Side.LIGHT, 3, 3, 3, 2, 6, "Commander Wedge Antilles", Uniqueness.UNIQUE, ExpansionSet.FIRST_ANTHOLOGY, Rarity.PV);
        setLore("Promoted to commander for his heroism in the Battle of Yavin. Leader in charge of training new pilots assigned to Echo Base. Piloted Rogue 3 in the Battle of Hoth.");
        setGameText("Adds 3 to power of any starship he pilots. On Rogue 3, also adds 2 to maneuver and draws one battle destiny if not able to otherwise. May use 2 Force to search your Reserve Deck and take one One More Pass into hand. Shuffle, cut and replace.");
        addPersona(Persona.WEDGE);
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.COMMANDER, Keyword.LEADER, Keyword.ROGUE_SQUADRON);
        setSpecies(Species.CORELLIAN);
        setMatchingVehicleFilter(Filters.Rogue_3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.starship));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.Rogue_3, Filters.hasPiloting(self)), 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Rogue_3), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.COMMANDER_WEDGE_ANTILLES__UPLOAD_ONE_MORE_PASS;

        // Check condition(s)
        if (GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take One More Pass into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.One_More_Pass, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
