package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ResetForfeitEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Weapon
 * Subtype: Character
 * Title: Darth Vader's Lightsaber
 */
public class Card1230_059 extends AbstractCharacterWeapon {
    public Card1230_059() {
        super(Side.DARK, 1, Title.Darth_Vaders_Lightsaber, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Vader's lightsaber. Symbol of the most feared man in the galaxy. Vader's control of the dark side of the Force allows him to wield this weapon in surprising ways.");
        setGameText("Deploy on Vader. May add 1 to Force drain where present. May target a Jedi or Skywalker for free. Draw 3 destiny and choose 2. Target hit (may place any Interrupt from opponent's Lost Pile out of play) and forfeit = 0, if total destiny > defense value.");
        addPersona(Persona.VADERS_LIGHTSABER);
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.LIGHTSABER);
        setMatchingCharacterFilter(Filters.Vader);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Vader);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Vader;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.Jedi, Filters.Skywalker), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.buildFireWeaponDrawXChooseYWithHitAction(3, 2, 0, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.wherePresent(self), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.justHitBy(game, effectResult, Filters.any, self)) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            if (cardHit != null) {
                final OptionalGameTextTriggerAction forfeitAction = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                forfeitAction.setText("Reset forfeit to 0");
                forfeitAction.appendEffect(
                        new ResetForfeitEffect(forfeitAction, cardHit, 0));
                actions.add(forfeitAction);
            }

            GameTextActionId gameTextActionId = GameTextActionId.LEGACY__VADER_SABER_V__PLACE_INTERRUPT;
            if (GameConditions.hasLostPile(game, opponent)) {
                final OptionalGameTextTriggerAction interruptAction = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                interruptAction.setText("Place Interrupt from Lost Pile out of play");
                interruptAction.appendEffect(
                        new PlaceCardOutOfPlayFromLostPileEffect(interruptAction, playerId, opponent, Filters.Interrupt, true));
                actions.add(interruptAction);
            }
        }
        return actions;
    }
}
