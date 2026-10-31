package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.FireWeaponFiredAtCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Effect
 * Title: Quick Draw
 */
public class Card1109_017 extends AbstractNormalEffect {
    public Card1109_017() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Quick Draw", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("It's high noon on Dagobah, his droid's reined in and Luke's got a fistful of credits.");
        setGameText("Deploy on table. Your lightsabers are destiny +2 and may target vehicles using 1 Force (each destiny draw is -2). If your character weapon (except a grenade) just lost, may use 1 Force to place that weapon in Used Pile. Once per game, may deploy Sai'torr Kal Fas from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyModifier(self, Filters.and(Filters.your(self), Filters.lightsaber), 2));
        modifiers.add(new MayBeTargetedByWeaponsModifier(self, Filters.and(Filters.your(self), Filters.lightsaber), Filters.and(Filters.opponents(self), Filters.vehicle)));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.your(self), Filters.lightsaber), Filters.any, -2, Filters.and(Filters.opponents(self), Filters.vehicle)));
        modifiers.add(new FireWeaponFiredAtCostModifier(self, Filters.and(Filters.your(self), Filters.lightsaber), 1, Filters.and(Filters.your(self), Filters.lightsaber), Filters.and(Filters.opponents(self), Filters.vehicle)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.your(self), Filters.character_weapon, Filters.not(Filters.titleContains("Grenade"))))
                && GameConditions.canUseForce(game, playerId, 1)) {
            final PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();
            if (justLostCard != null) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place weapon in Used Pile");
                action.setActionMsg("Use 1 Force to place " + GameUtils.getCardLink(justLostCard) + " in Used Pile");
                action.appendCost(
                        new UseForceEffect(action, playerId, 1));
                action.appendEffect(
                        new PutCardFromLostPileInUsedPileEffect(action, playerId, justLostCard, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__QUICK_DRAW__DEPLOY_SAITORR;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Saitorr_Kal_Fas)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Sai'torr Kal Fas from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Saitorr_Kal_Fas, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
