package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Character
 * Subtype: Rebel
 * Title: Commander Vanden Willard
 */
public class Card1103_007 extends AbstractRebel {
    public Card1103_007() {
        super(Side.LIGHT, 2, 3, 2, 2, 4, "Commander Vanden Willard", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Leader of Rebel forces on Yavin 4. Formerly Suolriep sector HQ commander. As a Rebel spy, aided Princess Leia and Bail Organa in the years prior to the Senate's dissolution.");
        setGameText("Leia is defense value and forfeit +3. Your senators at battleground sites and characters at Yavin 4 locations are forfeit +2. Once per game, may take a non-[Permanent Weapon] Leia into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.WARRIOR, Icon.A_NEW_HOPE);
        addKeywords(Keyword.COMMANDER, Keyword.LEADER, Keyword.SPY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, Filters.Leia, 3));
        ForfeitModifier leiaForfeit = new ForfeitModifier(self, Filters.Leia, 3);
        leiaForfeit.setClauseIdentity("leia");
        modifiers.add(leiaForfeit);
        ForfeitModifier senatorForfeit = new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.senator, Filters.at(Filters.battleground_site)), 2);
        senatorForfeit.setClauseIdentity("senators-battleground");
        modifiers.add(senatorForfeit);
        ForfeitModifier yavinForfeit = new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.Yavin_4_location)), 2);
        yavinForfeit.setClauseIdentity("yavin4-characters");
        modifiers.add(yavinForfeit);
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__COMMANDER_VANDEN_WILLARD_V__TAKE_LEIA;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Leia into hand from Reserve Deck");
            action.setActionMsg("Take a non-[Permanent Weapon] Leia into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.Leia, Filters.not(Icon.PERMANENT_WEAPON)), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
