package com.gempukku.swccgo.cards.set1102.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 2: Our Most Desperate Hour
 * Type: Character
 * Subtype: Rebel
 * Title: General Dodonna
 */
public class Card1102_007 extends AbstractRebel {
    public Card1102_007() {
        super(Side.LIGHT, 2, 3, 2, 2, 5, Title.General_Dodonna, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Skilled tactician and natural leader. Planned attack on Death Star after analyzing technical readouts provided by Princess Leia. Star Destroyer captain during Old Republic.");
        setGameText("During your control phase, if at a Rebel Base location, may use 1 Force to take one Colonel Feyn Gospic, an Admiral's Order, or a Rebel pilot of ability < 3 into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.GENERAL, Keyword.LEADER);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__GENERAL_DODONNA_V__TAKE_CARD;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isAtLocation(game, self, Filters.Rebel_Base_location)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Colonel Feyn Gospic, an Admiral's Order, or a Rebel pilot of ability < 3 into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.title("Colonel Feyn Gospic"), Filters.Admirals_Order, Filters.and(Filters.Rebel, Icon.PILOT, Filters.abilityLessThan(3))), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
