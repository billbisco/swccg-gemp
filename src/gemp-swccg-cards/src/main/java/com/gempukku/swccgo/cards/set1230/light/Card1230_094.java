package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitedToUsedPileModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitValueToUseWhenForfeitedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Superficial Damage
 */
public class Card1230_094 extends AbstractNormalEffect {
    public Card1230_094() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Superficial Damage", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Veteran Rebel engineers know that weapon systems positioned for adequate coverage are vulnerable themselves.");
        setGameText("Deploy on table. You may forfeit non-lightsaber weapons (using forfeit value = 3); place your just forfeited weapon in Used Pile. During your deploy phase, if you just deployed a vehicle or starship, may deploy a weapon (for free) on that vehicle or starship from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitValueToUseWhenForfeitedModifier(self,
                Filters.and(Filters.your(self), Filters.weapon, Filters.not(Filters.lightsaber)), 3));
        modifiers.add(new ForfeitedToUsedPileModifier(self, Filters.and(Filters.your(self), Filters.weapon)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__SUPERFICIAL_DAMAGE__DEPLOY_WEAPON;
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && TriggerConditions.justDeployed(game, effectResult, playerId, Filters.or(Filters.vehicle, Filters.starship))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PhysicalCard justDeployed = ((PlayCardResult) effectResult).getPlayedCard();
            if (justDeployed != null) {
                final OptionalGameTextTriggerAction deployWeapon = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                deployWeapon.setText("Deploy a weapon for free");
                deployWeapon.setActionMsg("Deploy a weapon for free on " + GameUtils.getCardLink(justDeployed) + " from Reserve Deck");
                deployWeapon.appendEffect(
                        new DeployCardToTargetFromReserveDeckEffect(deployWeapon, Filters.weapon, Filters.sameCardId(justDeployed), true, true));
                return Collections.singletonList(deployWeapon);
            }
        }
        return null;
    }
}
