package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableUsingForfeitValueEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitedToUsedPileModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/** Legacy Block 6.1: ISB Blaster Pistol. */
public class Card1261_064 extends AbstractCharacterWeapon {
    public Card1261_064() {
        super(Side.DARK, 5, "ISB Blaster Pistol", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Relby-k23 blaster pistol. Inflicts severe pain prior to death. Safety known to fail. Although these blasters are on the civilian restricted list, Imperial agents use them anyway.");
        setGameText("Deploy on Tarkin or an ISB Agent. May be forfeited to Used Pile (forfeit = 3). May target a character or creature for free. Draw destiny. Target hit, and its forfeit = 0, if destiny (+1 if on Tarkin) > defense value. Matching weapon for Tarkin.");
        addIcons(Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6);
        addKeywords(Keyword.BLASTER);
        setMatchingCharacterFilter(Filters.Tarkin);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId option, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Tarkin, Filters.ISB_agent));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(SwccgGame game, PhysicalCard self) {
        return Filters.or(Filters.Tarkin, Filters.ISB_agent);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new ForfeitedToUsedPileModifier(self, self));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, result, playerId)
                && (GameConditions.isBattleDamageRemaining(game, playerId) || GameConditions.isAttritionRemaining(game, playerId))
                && Filters.participatingInBattle.accepts(game, self)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, sourceId);
            action.setText("Forfeit ISB Blaster Pistol");
            action.appendEffect(new ForfeitCardFromTableUsingForfeitValueEffect(action, self, 3));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self,
            boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring,
            Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter targetFilter, boolean ignoreLimit) {
        FireWeaponActionBuilder builder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self,
                forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, targetFilter, ignoreLimit)
                .targetUsingForce(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), 0, TargetingReason.TO_BE_HIT)
                .finishBuildPrep();
        if (builder == null) return null;
        int bonus = Filters.Tarkin.accepts(game, self.getAttachedTo()) ? 1 : 0;
        return Collections.singletonList(builder.buildFireWeaponWithHitAction(1, bonus, Statistic.DEFENSE_VALUE, true, 0));
    }
}
