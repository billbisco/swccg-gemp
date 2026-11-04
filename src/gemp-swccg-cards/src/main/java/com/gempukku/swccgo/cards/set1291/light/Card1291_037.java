package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyAndChooseInsteadEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Character
 * Subtype: Republic
 * Title: Clone Sniper
 */
public class Card1291_037 extends AbstractRepublic {
    public Card1291_037() {
        super(Side.LIGHT, 3, 3, 3, 2, 3, "Clone Sniper", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setArmor(4);
        setLore("Clone trooper and scout.");
        setGameText("When deployed, may deploy A280 Sharpshooter Rifle on him from Reserve Deck; reshuffle. A280 Sharpsooter Rifle may deploy on Clone Sniper. When firing a rifle at an adjacent site, may draw two weapon destiny and choose one. Immune to Undercover.");
        addIcons(Icon.WARRIOR, Icon.EPISODE_I, Icon.CLONE_ARMY);
        addKeywords(Keyword.CLONE_TROOPER, Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.A280_Sharpshooter_Rifle, Filters.sameCardId(self)));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Undercover));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        // When deployed, may deploy A280 Sharpshooter Rifle on him from Reserve Deck
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CLONE_SNIPER__DEPLOY_A280;
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy A280 Sharpshooter Rifle from Reserve Deck");
            action.setActionMsg("Deploy A280 Sharpshooter Rifle on " + GameUtils.getCardLink(self) + " from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.A280_Sharpshooter_Rifle, Filters.sameCardId(self), true));
            actions.add(action);
        }

        // When firing a rifle at an adjacent site, may draw two weapon destiny and choose one
        Filter rifleFiredByThisCharacter = Filters.and(Filters.rifle, Filters.weaponBeingFiredBy(self));
        if (TriggerConditions.isAboutToDrawWeaponDestiny(game, effectResult, playerId, rifleFiredByThisCharacter)
                && GameConditions.isDuringWeaponFiringAtTarget(game, rifleFiredByThisCharacter, Filters.at(Filters.adjacentSite(self)))
                && GameConditions.canDrawDestinyAndChoose(game, 1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw two weapon destiny and choose one");
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyAndChooseInsteadEffect(action, 2, 1));
            actions.add(action);
        }

        return actions.isEmpty() ? null : actions;
    }
}
