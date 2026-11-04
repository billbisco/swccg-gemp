package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacyVb73Light;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyDrawForActionSourceModifier;
import com.gempukku.swccgo.logic.modifiers.FireWeaponFiredByForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Objective
 * Title: Center Of Tyranny / A Liberated World
 */
public class Card1273_042 extends AbstractObjective {
    public static final String SHEET_GAMETEXT = "Center Of Tyranny: Deploy [Virtual Block 7] Coruscant, Main Power Plant, Lower Levels, Planetary Shield, and Rogue Insertion. For remainder of game, Rogue Squadron pilots are Insurgents and may fire blasters for free. Your destiny draws for [Virtual Block 1] Sabotage are -1 and may not be further reduced. Your characters of ability > 4 are deploy +4. While this side up, during your deploy phase, may use 1 Force to deploy a Coruscant battleground site from Reserve Deck; reshuffle. Flip this card if Planetary Shield 'disabled.' A Liberated World: Immediately retrieve into hand (or take from Reserve Deck; reshuffle) any one card. While this side up, '1' on Declaration Of Rebellion is treated as '2'. Your Force generation at Coruscant system is +2. While you occupy a system, your Force drains are +1 at each related battleground site you control with a Rogue Squadron pilot. Once per turn, if opponent just lost a starship from a system, you may lose 1 Force to steal it into hand. Once during your turn, if you just forfeited a Rogue Squadron pilot, may use 1 Force to take any one card into hand from Reserve Deck; reshuffle.";

    public Card1273_042() {
        super(Side.LIGHT, 0, LegacyVb73Light.CENTER_OF_TYRANNY, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText(SHEET_GAMETEXT);
        addIcons(Icon.SPECIAL_EDITION);
    }

    /**
     * (Virtual Block 1) Sabotage.
     */
    static Filter sabotage() {
        return Filters.and(Filters.Sabotage, Icon.LEGACY_BLOCK_1);
    }

    /**
     * The "for remainder of game" modifiers.
     */
    static List<Modifier> remainderOfGameModifiers(PhysicalCard self, String playerId) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, Filters.Rogue_Squadron_pilot, Keyword.INSURGENT));
        modifiers.add(new FireWeaponFiredByForFreeModifier(self, Filters.Rogue_Squadron_pilot, Filters.blaster));
        modifiers.add(new DestinyDrawForActionSourceModifier(self, sabotage(), -1, playerId));
        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.your(playerId), Filters.character, Filters.abilityMoreThan(4)), 4));
        return modifiers;
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Coruscant_system, Icon.LEGACY_BLOCK_7), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Coruscant system to deploy";
                    }

                    @Override
                    protected void cardDeployed(PhysicalCard card) {
                        action.appendRequiredEffect(
                                new DeployCardToSystemFromReserveDeckEffect(action, Filters.title(LegacyVb73Light.MAIN_POWER_PLANT), Title.Coruscant, true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Choose Main Power Plant to deploy";
                                    }
                                });
                        action.appendRequiredEffect(
                                new DeployCardToSystemFromReserveDeckEffect(action, Filters.title(LegacyVb73Light.LOWER_LEVELS), Title.Coruscant, true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Choose Lower Levels to deploy";
                                    }
                                });
                        action.appendRequiredEffect(
                                new DeployCardFromReserveDeckEffect(action, LegacyVb73Light.planetaryShield(), true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Choose Planetary Shield to deploy";
                                    }
                                });
                        action.appendRequiredEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.title(LegacyVb73Light.ROGUE_INSERTION), true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Choose Rogue Insertion to deploy";
                                    }
                                });
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        for (Modifier modifier : remainderOfGameModifiers(self, playerId)) {
            action.appendEffect(
                    new AddUntilEndOfGameModifierEffect(action, modifier, null));
        }
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CENTER_OF_TYRANNY__DEPLOY_CORUSCANT_BATTLEGROUND_SITE;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Coruscant battleground site from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Coruscant_site, Filters.battleground), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Flip this card if Planetary Shield 'disabled' (the 'disabled' result places Planetary Shield out of play from table)
        if (TriggerConditions.justPlacedOutOfPlayFromTable(game, effectResult, LegacyVb73Light.planetaryShield())
                && GameConditions.canBeFlipped(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
