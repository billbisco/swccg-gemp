package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractAlienRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
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
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Alien/Rebel
 * Title: Chewbacca
 */
public class Card1210_093 extends AbstractAlienRebel {
    public Card1210_093() {
        super(Side.LIGHT, 1, 4, 6, 2, 6, "Chewbacca", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Wookiee smuggler from Kashyyyk. Over 200 years old. Top-notch mechanic and pilot. Jabba has large bounty on this 'walking carpet'. Friends call him Chewie...or Fuzzball.");
        setGameText("Adds 2 to power of anything he pilots. During your turn, may use 1 Force to take one Chewie's AT-ST, Chewbacca's Bowcaster, That's One, or an Interrupt with 'Chewie' or 'Wookiee' in game text into hand from Reserve Deck; reshuffle.");
        addPersona(Persona.CHEWIE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.A_NEW_HOPE);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.WOOKIEE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CHEWBACCA_V__TAKE_CARD;
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take a card into hand from Reserve Deck");
            action.setActionMsg("Take Chewie's AT-ST, Chewbacca's Bowcaster, That's One, or an Interrupt with 'Chewie' or 'Wookiee' in game text into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                            Filters.or(Filters.title("Chewie's AT-ST"), Filters.title("Chewbacca's Bowcaster"), Filters.title("That's One"),
                                    Filters.and(Filters.Interrupt, Filters.or(Filters.gameTextContains("Chewie"), Filters.gameTextContains("Wookiee")))), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
