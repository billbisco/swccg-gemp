package com.gempukku.swccgo.cards.set1108.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.SatisfiesAllBattleDamageWhenForfeitedModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Character
 * Subtype: Alien
 * Title: Kalit
 */
public class Card1108_009 extends AbstractAlien {
    public Card1108_009() {
        super(Side.LIGHT, 2, 2, 2, 2, 5, Title.Kalit, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jawa leader. Seeking to peacefully settle a long standing disagreement with his rival, Wittin. Wants Jabba to mediate their talks.");
        setGameText("Deploys only on Tatooine. Your other Jawas are power and forfeit +1. Your vehicle forfeited at same site satisfies all remaining battle damage against you. During your turn, may use 1 Force to take a card with 'Jawa' in game text into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.JAWA);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourOtherJawas = Filters.and(Filters.your(self), Filters.other(self), Filters.Jawa);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, yourOtherJawas, 1));
        modifiers.add(new ForfeitModifier(self, yourOtherJawas, 1));
        modifiers.add(new SatisfiesAllBattleDamageWhenForfeitedModifier(self, Filters.and(Filters.your(self), Filters.vehicle, Filters.atSameSite(self))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__KALIT_V__TAKE_JAWA;

        if (GameConditions.isDuringYourTurn(game, playerId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a card with 'Jawa' in game text into hand from Reserve Deck");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.gameTextContains("Jawa"), true));
            return java.util.Collections.singletonList(action);
        }
        return null;
    }
}
