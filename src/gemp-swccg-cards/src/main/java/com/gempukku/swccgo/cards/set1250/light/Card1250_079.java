package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.InitiateBattleCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 5
 * Type: Starship
 * Subtype: Capital
 * Title: Alderaan Consular Ship
 * Every publication is a version (Bill, 2026-11-11): the Virtual Cards Master List (2013-04-27, PC-E2) repeats the
 * release-sheet wording word for word (2250_79) and is the current text; the PC website card-list text
 * (PC-E1) is 2250_1079. Same play.
 */
public class Card1250_079 extends AbstractCapitalStarship {
    public Card1250_079() {
        super(Side.LIGHT, 2, 4, 5, 5, null, 3, 8, "Alderaan Consular Ship", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setGameText("May add 3 pilots and 4 passengers. Permanent pilot provides ability of 2. Opponent must use +2 Force to initiate battle here. Once per game, may deploy a senator aboard from Reserve Deck; reshuffle.");
        addIcons(Icon.DEATH_STAR_II, Icon.EPISODE_I, Icon.REPUBLIC, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.CORELLIAN_CORVETTE);
        setPilotCapacity(3);
        setPassengerCapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new InitiateBattleCostModifier(self, Filters.here(self), 2, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsEvenIfUnpiloted(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ALDERAAN_CONSULAR_SHIP__DEPLOY_SENATOR;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a senator aboard");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new DeployCardAboardFromReserveDeckEffect(action, Filters.and(Filters.senator, Filters.canBeTargetedBy(self)), Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
