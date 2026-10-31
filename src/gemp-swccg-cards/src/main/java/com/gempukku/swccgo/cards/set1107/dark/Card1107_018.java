package com.gempukku.swccgo.cards.set1107.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 7: Shadows Of The Empire
 * Type: Character
 * Subtype: Alien
 * Title: Prince Xizor (V)
 */
public class Card1107_018 extends AbstractAlien {
    public Card1107_018() {
        super(Side.DARK, 1, 4, 5, 3, 6, Title.Xizor, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_7, Rarity.V);
        setVirtualSuffix(true);
        setLore("Falleen gangster and leader. Black Sun agent. Dark Prince of the Black Sun crime syndicate. Fortifies his personal defenses with information gathered by his agents.");
        setGameText("Adds 2 to power of anything he pilots. Other Black Sun agents are forfeit +1. Your unique (•) gamblers, gangsters, smugglers, and thieves are Black Sun agents. Once per turn, may ▲ Unexpected Interruption. Immune to attrition < 4.");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GANGSTER, Keyword.LEADER, Keyword.BLACK_SUN_AGENT);
        setSpecies(Species.FALLEEN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.Black_Sun_agent, Filters.not(self)), 1));
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.your(self), Filters.unique, Filters.or(Filters.gambler, Filters.gangster, Filters.smuggler, Filters.thief)), Keyword.BLACK_SUN_AGENT));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.OVS__PRINCE_XIZOR__TAKE_UNEXPECTED_INTERRUPTION;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Unexpected Interruption into hand");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title("Unexpected Interruption"), true));
            actions.add(action);
        }
        return actions;
    }
}
