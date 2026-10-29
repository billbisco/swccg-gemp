package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Advantage
 */
public class Card1220_111 extends AbstractNormalEffect {
    public Card1220_111() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Advantage, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("With only a few Jedi remaining in the galaxy, each confrontation between them has far reaching consequences.");
        setGameText("Deploy on your battleground. Once per battle, if opponent's card is hit by a non-[Permanent Weapon] weapon, may make opponent's total power -4 (-2 if hit by a lightsaber).");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.battleground, Filters.occupies(self.getOwner()));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.battleground;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && TriggerConditions.justHitBy(game, effectResult, Filters.opponents(self), Filters.and(Filters.weapon, Filters.not(Icon.PERMANENT_WEAPON)))
                && ((HitResult) effectResult).getHitByPermanentWeapon() == null) {
            PhysicalCard hitBy = ((HitResult) effectResult).getHitByCard();
            int amount = (hitBy != null && Filters.lightsaber.accepts(game, hitBy)) ? -2 : -4;
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent's total power " + amount);
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new TotalPowerModifier(self, Filters.battleLocation, amount, opponent),
                            "makes opponent's total power " + amount));
            return Collections.singletonList(action);
        }
        return null;
    }
}
