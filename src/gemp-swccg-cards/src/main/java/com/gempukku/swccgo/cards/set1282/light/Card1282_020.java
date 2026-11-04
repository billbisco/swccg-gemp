package com.gempukku.swccgo.cards.set1282.light;

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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.IgnoresDeploymentRestrictionsFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Effect
 * Title: A Remote Planet (V)
 */
public class Card1282_020 extends AbstractNormalEffect {
    public Card1282_020() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "A Remote Planet", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("In unfamiliar and potentially hostile territory, Qui-Gon knew that success would require patience and caution.");
        setGameText("Deploy on The Hyperdrive Generator's Gone; treat \"4\" on it as \"2.\" Your Force generation at City Outskirts is +1. May deploy systems regardless of your Objective deployment restrictions. Once per game, may deploy Qui-Gon or Obi-Wan (and/or their matching weapon) from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.EPISODE_I, Icon.SETUP);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.The_Hyperdrive_Generators_Gone;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.hasAttached(self), ModifyGameTextType.LEGACY__HYPERDRIVE_GENERATORS_GONE__TREAT_4_AS_2));
        modifiers.add(new ForceGenerationModifier(self, Filters.City_Outskirts, 1, playerId));
        modifiers.add(new IgnoresDeploymentRestrictionsFromCardModifier(self, Filters.and(Filters.your(playerId), Filters.system), null, playerId, Filters.Objective));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__A_REMOTE_PLANET_V__DEPLOY_QUIGON_OR_OBIWAN;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            Filter quiGonOrObiWan = Filters.or(Filters.QuiGon, Filters.ObiWan);
            List<Filter> weaponFilters = new LinkedList<Filter>();
            for (PhysicalCard character : Filters.filterActive(game, self, Filters.and(Filters.your(playerId), quiGonOrObiWan))) {
                weaponFilters.add(Filters.matchingWeaponForCharacter(character));
            }
            Filter cardToDeploy = weaponFilters.isEmpty() ? quiGonOrObiWan : Filters.or(quiGonOrObiWan, Filters.or(weaponFilters.toArray(new Filter[0])));

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Qui-Gon or Obi-Wan (and/or matching weapon) from Reserve Deck");
            action.setActionMsg("Deploy Qui-Gon or Obi-Wan (and/or their matching weapon) from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, cardToDeploy, true) {
                        @Override
                        protected void cardDeployed(final PhysicalCard deployedCard) {
                            if (Filters.or(Filters.QuiGon, Filters.ObiWan).accepts(game, deployedCard)) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new YesNoDecision("Do you want to deploy a matching weapon from Reserve Deck?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.appendEffect(
                                                                new DeployCardToTargetFromReserveDeckEffect(action, Filters.matchingWeaponForCharacter(deployedCard), Filters.sameCardId(deployedCard), false, true));
                                                    }
                                                }));
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
