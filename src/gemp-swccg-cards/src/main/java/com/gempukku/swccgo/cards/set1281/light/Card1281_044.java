package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Objective
 * Title: Infiltration / Unlikely Allies
 */
public class Card1281_044 extends AbstractObjective {
    public static final String PACK_GAMETEXT = "Infiltration: Deploy Nar Shaddaa, Undercity and Scoundrel's Luck. {For} remainder of game, your gamblers, information brokers, and spies are scoundrels. You may not deploy Nightfall or cards with ability except scoundrels and Independent Starships. 'Luke' on opponent's (Reflection II) objective is treated as 'Kyle Katarn.' Once per turn, may deploy a battleground site to Nar Shaddaa from Reserve Deck; reshuffle. {While} this side up, your Force drains at systems are -1. {Flip} this card if If Imperial Navigation Charts is 'stolen'. Unlikely Allies: {While} this side up, your scoundrels are forfeit +1. Your Force Generation is +1 for each battlegound you occupy with a scoundrel (to a maximum of +2). 'Once' on Scoundrel's Luck is treated as 'twice.' During your control phase, may retrieve 1 Force. Once during your turn, may place a card from hand in your Used Pile or Lost Pile, shuffle that pile and take the top card into hand. {Flip} this card if Imperial Navigation Charts is not on table (unless you have completed a (v) Kessel Run).";

    public Card1281_044() {
        super(Side.LIGHT, 0, "Infiltration", ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText(PACK_GAMETEXT);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.system, Filters.title(Title.Nar_Shaddaa)), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Nar Shaddaa to deploy";
                    }

                    @Override
                    protected void cardDeployed(PhysicalCard card) {
                        action.appendRequiredEffect(
                                new DeployCardToSystemFromReserveDeckEffect(action, Filters.title("Nar Shaddaa: Undercity"), Title.Nar_Shaddaa, true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Choose Undercity to deploy";
                                    }
                                });
                        action.appendRequiredEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.title("Scoundrel's Luck"), true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Choose Scoundrel's Luck to deploy";
                                    }
                                });
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new KeywordModifier(self, Filters.and(Filters.your(playerId),
                                Filters.or(Filters.gambler, Filters.information_broker, Filters.spy)), Keyword.SCOUNDREL), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.title(Title.Nightfall), playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.and(
                                Filters.hasAbilityOrHasPermanentPilotWithAbility,
                                Filters.not(Filters.or(Filters.scoundrel, Filters.and(Filters.starship, Icon.INDEPENDENT)))), playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new ModifyGameTextModifier(self, Filters.and(Filters.opponents(playerId), Filters.Objective, Icon.REFLECTIONS_II),
                                ModifyGameTextType.REFLECTIONS_II_OBJECTIVE__TARGETS_KYLE_INSTEAD_OF_LUKE), null));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.system, -1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        return battlegroundSiteAction(playerId, game, self, gameTextSourceCardId);
    }

    static List<TopLevelGameTextAction> battlegroundSiteAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__INFILTRATION__DEPLOY_BATTLEGROUND_SITE;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy battleground site to Nar Shaddaa");
            action.setActionMsg("Deploy a battleground site to Nar Shaddaa from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardToSystemFromReserveDeckEffect(action, Filters.battleground_site, Title.Nar_Shaddaa, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && chartsStolen(game, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            action.appendEffect(new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    static boolean chartsStolen(SwccgGame game, PhysicalCard self) {
        PhysicalCard charts = Filters.findFirstActive(game, self, Filters.title("Imperial Navigation Charts"));
        return charts != null && (charts.isStolen() || charts.getZone() == Zone.SIDE_OF_TABLE);
    }
}
