package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Weapon
 * Subtype: Character
 * Title: A280 Sharpshooter Rifle
 */
public class Card1109_001 extends AbstractCharacterWeapon {
    public Card1109_001() {
        super(Side.LIGHT, 3, Title.A280_Sharpshooter_Rifle, Uniqueness.RESTRICTED_2, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("BlasTech sharpshooter rifle accurate to 500 meters. Corporal Janse brought several A280s with him when he left BlasTech.");
        setGameText("Deploy on your Rebel scout. During battles at same site, attrition against opponent is +1. May target a character, creature or vehicle at same or adjacent site for free. Draw destiny. Target hit if destiny +2 > defense value. Once during your control phase, may fire using 2 Force.");
        addIcons(Icon.ENDOR);
        addKeyword(Keyword.RIFLE);
    }

    private Filter rebelScout() {
        return Filters.and(Filters.Rebel, Filters.scout);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), rebelScout());
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return rebelScout();
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetAtSameOrAdjacentSiteForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature, Filters.vehicle), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, 2, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, Filters.sameSite(self), 1, opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        Filter targetFilter = Filters.or(Filters.character, Filters.creature, Filters.vehicle);

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 2)
                && Filters.canBeFired(self, 0).accepts(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Fire during control phase");
            action.setActionMsg("Fire " + GameUtils.getCardLink(self));
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            action.appendEffect(
                    new FireWeaponEffect(action, self, true, targetFilter));
            return Collections.singletonList(action);
        }
        return null;
    }
}
