package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyAndChooseInsteadEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeExcludedFromBattleByPlayerModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveDeployCostIncreasedAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Legacy Block 6.1 Carnor Jax, Royal Guard. */
public class Card1261_008 extends AbstractImperial {
    public Card1261_008() {
        super(Side.DARK, 2, 4, 5, 2, 5, "Carnor Jax, Royal Guard",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setArmor(3);
        setGameText("Imperials may not have their deploy costs increased (or be excluded from battle) by opponent here. When using a Force Pike, may draw two weapon destiny and choose one (may take the other into hand). Immune to attrition < 3.");
        addIcons(Icon.WARRIOR, Icon.DEATH_STAR_II);
        addKeywords(Keyword.ROYAL_GUARD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        return List.of(
                new MayNotHaveDeployCostIncreasedAtLocationModifier(self, Filters.Imperial, Filters.here(self), opponent),
                new MayNotBeExcludedFromBattleByPlayerModifier(self, Filters.and(Filters.Imperial, Filters.here(self)), opponent),
                new ImmuneToAttritionLessThanModifier(self, 3));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.isAboutToDrawWeaponDestiny(game, effectResult, playerId, Filters.title("Force Pike"))
                || !GameConditions.canDrawDestinyAndChoose(game, 1))
            return null;
        WeaponFiringState firing = game.getGameState().getWeaponFiringState();
        if (firing == null || firing.getCardFiringWeapon() == null
                || firing.getCardFiringWeapon().getCardId() != self.getCardId())
            return null;
        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Draw two weapon destiny and choose one");
        action.appendEffect(DrawDestinyAndChooseInsteadEffect.mayTakeOtherIntoHand(action, 2, 1));
        return List.of(action);
    }
}
