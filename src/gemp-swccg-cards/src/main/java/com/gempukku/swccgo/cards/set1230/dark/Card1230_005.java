package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.UndercoverCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Alien
 * Title: Fozec
 */
public class Card1230_005 extends AbstractAlien {
    public Card1230_005() {
        super(Side.DARK, 2, 2, 1, 1, 3, Title.Fozec, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Spy for the Empire. Keeping tabs on Jabba's activities for the ISB. Secretly hoping to leave the Empire and pursue lucrative opportunities in the underworld.");
        setGameText("Adds 2 to power of anything he pilots. While Undercover, opponent's Force drains at related locations are -1. Once during battle, if with an ISB agent, may cause opponent to draw destiny. You may retrieve 2 Force if destiny > 2.");
        addIcons(Icon.PILOT, Icon.JABBAS_PALACE);
        addKeywords(Keyword.SPY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForceDrainModifier(self, Filters.relatedLocation(self), new UndercoverCondition(self), -1, opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__FOZEC_V__DRAW_DESTINY;

        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.isInBattleWith(game, self, Filters.ISB_agent)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Cause opponent to draw destiny");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendEffect(
                    new DrawDestinyEffect(action, opponent) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            if (totalDestiny != null && totalDestiny > 2) {
                                action.appendEffect(
                                        new RetrieveForceEffect(action, playerId, 2));
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
