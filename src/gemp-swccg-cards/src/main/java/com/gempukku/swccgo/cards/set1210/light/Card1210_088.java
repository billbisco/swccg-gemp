package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Rebel
 * Title: Biggs Darklighter
 */
public class Card1210_088 extends AbstractRebel {
    public Card1210_088() {
        super(Side.LIGHT, 2, 2, 2, 2, 5, Title.Biggs, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Piloted Red 3 at Battle of Yavin. Childhood friend of Luke. Led mutiny on Rand Ecliptic and theft of Ecliptic from shipyards on Bestine. Ecliptic Evaders emblem on helmet.");
        setGameText("Adds 2 to power of anything he pilots. Deploys free to Beggar's Canyon, Red 3, or same location as Fel or Hobbie. While piloting Red 3, it has an [Nav Computer] aboard. During your control phase, may take one Old Times into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeyword(Keyword.RED_SQUADRON);
        setMatchingStarshipFilter(Filters.Red_3);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.or(Filters.Beggars_Canyon, Filters.Red_3, Filters.sameLocationAs(self, Filters.or(Filters.Fel, Filters.Hobbie)))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingRed3 = new PilotingCondition(self, Filters.Red_3);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new IconModifier(self, Filters.and(Filters.Red_3, Filters.hasPiloting(self)), pilotingRed3, Icon.NAV_COMPUTER));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BIGGS_DARKLIGHTER_V__TAKE_OLD_TIMES;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Old Times into hand");
            action.setActionMsg("Take Old Times into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title("Old Times"), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
