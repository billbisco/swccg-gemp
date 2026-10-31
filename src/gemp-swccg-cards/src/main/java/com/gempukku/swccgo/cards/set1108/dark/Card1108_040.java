package com.gempukku.swccgo.cards.set1108.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCoveredByHothEnergyShieldModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Character
 * Subtype: Alien
 * Title: J'Quille
 */
public class Card1108_040 extends AbstractAlien {
    public Card1108_040() {
        super(Side.DARK, 2, 4, 5, 1, 4, Title.Jquille, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Whiphid spy in league with Lady Valarian. Yearns for battle. Enjoys hearing the screams of his victims. Plotting to kill Jabba.");
        setGameText("May deploy to a shielded site, and, for remainder of that turn, suspends 'Hoth Energy Shield Rules' at his site. Once per game, when deployed to a site, opponent loses 1 Force for each Light Side character present.");
        addIcons(Icon.WARRIOR, Icon.JABBAS_PALACE);
        addKeywords(Keyword.SPY);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier(self, self, Filters.shielded_location));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployedToLocation(game, effectResult, self, Filters.site)) {
            PhysicalCard site = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);
            if (site != null) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Suspend Hoth Energy Shield Rules here");
                action.appendEffect(
                        new AddUntilEndOfTurnModifierEffect(action,
                                new MayNotBeCoveredByHothEnergyShieldModifier(self, site),
                                "Suspends 'Hoth Energy Shield Rules' at " + GameUtils.getCardLink(site) + " for remainder of turn"));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__JQUILLE_V__DEPLOY_TO_SITE;
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.justDeployedToLocation(game, effectResult, self, Filters.site)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            int num = Filters.countActive(game, self, Filters.and(Filters.character, Filters.owner(game.getLightPlayer()), Filters.present(self)));
            if (num > 0) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Opponent loses " + num + " Force");
                action.appendUsage(
                        new OncePerGameEffect(action));
                action.appendEffect(
                        new LoseForceEffect(action, opponent, num));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
