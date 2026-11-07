package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.List;

/** Legacy Block 6.1 Droid Racks (V). */
public class Card1261_031 extends AbstractNormalEffect {
    public Card1261_031() {
        super(Side.DARK, 1, PlayCardZoneOption.ATTACHED, Title.Droid_Racks, Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Trade Federation utilizes such efficient methods of droid deployment that it is rumored even they do not know exactly how many battle droids are in circulation.");
        setGameText("Deploy on Invasion. Where Are Those Droidekas?! ignores Neimoidians. If your destroyer droid was just lost, may stack it here. During your control phase, may use 2 Force to place any destroyer droid here in Used Pile. If your vehicle or character (except a Neimoidian or destroyer droid) at a site, this Effect lost. (Immune to Alter.)");
        addIcons(Icon.EPISODE_I, Icon.THEED_PALACE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self,
            PlayCardOptionId id, boolean asReact) {
        return Filters.Invasion;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ModifyGameTextModifier(self, Filters.Where_Are_Those_Droidekas,
                ModifyGameTextType.LEGACY__DROID_RACKS__WHERE_ARE_THOSE_DROIDEKAS_IGNORES_NEIMOIDIANS));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.justLost(game, result, Filters.and(Filters.your(self), Filters.destroyer_droid)))
            return null;
        var droid = ((LostFromTableResult) result).getCard();
        var action = new OptionalGameTextTriggerAction(self, sourceId, GameTextActionId.OTHER_CARD_ACTION_1);
        action.setText("Stack " + GameUtils.getFullName(droid));
        action.appendEffect(new StackOneCardFromLostPileEffect(action, droid, self, false, true, true));
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int sourceId) {
        if (!GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                || !GameConditions.canUseForce(game, playerId, 2)
                || !GameConditions.hasStackedCards(game, self, Filters.destroyer_droid))
            return null;
        var action = new TopLevelGameTextAction(self, playerId, sourceId, GameTextActionId.OTHER_CARD_ACTION_2);
        action.setText("Place a destroyer droid in Used Pile");
        action.appendTargeting(new ChooseStackedCardEffect(action, playerId, self, Filters.destroyer_droid) {
            @Override
            protected void cardSelected(PhysicalCard droid) {
                action.appendCost(new UseForceEffect(action, playerId, 2));
                action.appendEffect(new PutStackedCardInUsedPileEffect(action, playerId, droid, false));
            }
        });
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isTableChanged(game, result) || !GameConditions.canSpot(game, self,
                Filters.and(Filters.your(self), Filters.at(Filters.site),
                        Filters.or(Filters.vehicle, Filters.and(Filters.character,
                                Filters.not(Filters.or(Filters.Neimoidian, Filters.destroyer_droid)))))))
            return null;
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.appendEffect(new LoseCardFromTableEffect(action, self));
        return List.of(action);
    }
}
