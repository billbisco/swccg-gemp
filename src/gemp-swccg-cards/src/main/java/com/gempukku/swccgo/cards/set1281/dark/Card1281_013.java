package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.InitiateBattlesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBePlacedInUsedPileModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: The Inner Circle
 */
public class Card1281_013 extends AbstractNormalEffect {
    public Card1281_013() {
        super(Side.DARK, 6, PlayCardZoneOption.ATTACHED, "The Inner Circle", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLore("Imperial officers aboard the Death Star considered the Rebellion a minor threat.");
        setGameText("Deploy on Conference Room (only at start of game); deploy Sim Aloo here and (Virtual Block 4) There Is No Try from Reserve Deck; reshuffle. Players initiate battles here for free. Once per game may deploy Ord Mantell from Reserve Deck; reshuffle. May not be canceled (except by opponent) or placed in Used Pile. Immune to Ineffective Maneuver.");
        addIcons(Icon.SETUP);
        addImmuneToCardTitle(Title.Ineffective_Maneuver);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.isDuringStartOfGame(game);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Death_Star_Conference_Room;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        // "May not be canceled (except by opponent)": its owner's cards (other than the site it is on) may not target it
        modifiers.add(new MayNotBeTargetedByModifier(self, self, Filters.and(Filters.owner(self.getOwner()), Filters.not(Filters.hasAttached(self)))));
        modifiers.add(new MayNotBePlacedInUsedPileModifier(self, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter here = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new InitiateBattlesForFreeModifier(self, here, playerId));
        modifiers.add(new InitiateBattlesForFreeModifier(self, here, opponent));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && self.getAttachedTo() != null) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(self.getOwner());
            action.setText("Deploy Sim Aloo and There Is No Try");
            action.setActionMsg("Deploy Sim Aloo here and There Is No Try from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.title("Sim Aloo"), Filters.sameCardId(self.getAttachedTo()), true, false));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.There_Is_No_Try, Icon.LEGACY_BLOCK_4), true, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THE_INNER_CIRCLE__DEPLOY_ORD_MANTELL;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Ord_Mantell)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Ord Mantell from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Ord_Mantell), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
