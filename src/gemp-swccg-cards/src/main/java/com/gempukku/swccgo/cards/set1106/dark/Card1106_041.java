package com.gempukku.swccgo.cards.set1106.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ExtraPermanentPilotModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Effect
 * Title: Kuat Drive Yards (V)
 */
public class Card1106_041 extends AbstractNormalEffect {
    public Card1106_041() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Kuat_Drive_Yards, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("Company that produces current generation of Star Destroyers, as well as Nebulon-B Frigates. Ship yards are extremely well defended.");
        setGameText("Deploy on table. Fear Will Keep Them In Line is canceled. Unique (•) Imperial-class Star Destroyers are deploy -1, gain an additional permanent pilot of ability 2, and are immune to attrition < 4. During your deploy phase, may use 2 Force to ▼ a battleground system. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter uniqueImperialClassStarDestroyer = Filters.and(Filters.unique, Filters.Imperial_class_Star_Destroyer);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, uniqueImperialClassStarDestroyer, -1));
        modifiers.add(new ExtraPermanentPilotModifier(self, uniqueImperialClassStarDestroyer, 2));
        modifiers.add(new IconModifier(self, uniqueImperialClassStarDestroyer, Icon.PILOT));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, uniqueImperialClassStarDestroyer, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.KUAT_DRIVE_YARDS__DOWNLOAD_BATTLEGROUND_SYSTEM;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 2)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy battleground system from Reserve Deck");
            action.setActionMsg("Deploy a battleground system from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.system, Filters.battleground, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.Fear_Will_Keep_Them_In_Line;

        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Fear_Will_Keep_Them_In_Line)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Fear_Will_Keep_Them_In_Line, Title.Fear_Will_Keep_Them_In_Line);
            return Collections.singletonList(action);
        }
        return null;
    }
}
