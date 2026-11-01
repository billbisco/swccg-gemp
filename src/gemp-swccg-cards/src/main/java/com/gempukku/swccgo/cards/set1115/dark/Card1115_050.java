package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Objective
 * Title: Imperial Entanglements / No One To Stop Us This Time
 */
public class Card1115_050 extends AbstractObjective {
    public Card1115_050() {
        super(Side.DARK, 0, "Imperial Entanglements", ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setVirtualSuffix(true);
        setGameText("Deploy Tatooine (with Devastator there) and a non-Jabba's Palace Tatooine battleground site. For remainder of game, you may not deploy systems, non-Imperial characters, non-Imperial starships, or Jabba's Palace sites. While this side up, once per turn, may deploy a Tatooine battleground site from Reserve Deck; reshuffle. Opponent loses no Force to Tatooine Occupation or your Force drains at Tatooine system. Flip this card if you control three Tatooine sites and opponent controls less than three Tatooine sites.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(new DeployCardFromReserveDeckEffect(action, Filters.Tatooine_system, true, false) {
            @Override
            public String getChoiceText() {
                return "Choose Tatooine to deploy";
            }
        });
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployModifier(self, Filters.or(Filters.system,
                Filters.and(Filters.character, Filters.not(Filters.Imperial)),
                Filters.and(Filters.starship, Filters.not(Filters.Imperial)),
                Filters.titleContains("Jabba's Palace")), playerId));
        modifiers.add(new CancelsGameTextModifier(self, Filters.title("Tatooine Occupation")));
        modifiers.add(new LimitForceLossFromForceDrainModifier(self, Filters.Tatooine_system, 0, opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.RALLTIIR_OPERATIONS__DOWNLOAD_SITE_OR_NONUNIQUE_IMPERIAL_TO_RALLTIIR;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Tatooine battleground site");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Tatooine_site, Filters.battleground), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controls(game, playerId, 3, Filters.Tatooine_site)
                && !GameConditions.controls(game, opponent, 3, Filters.Tatooine_site)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.appendEffect(new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
