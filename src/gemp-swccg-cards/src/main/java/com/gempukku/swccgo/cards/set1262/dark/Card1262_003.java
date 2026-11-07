package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFlippedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.List;

/** Legacy Block 6.2: Altering The Deal. */
public class Card1262_003 extends AbstractNormalEffect {
    public Card1262_003() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Altering The Deal", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setLore("Gamblers are vulnerable to bribery, extortion and other forms of manipulation. Their notorious dealings can be easily taken advantage of.");
        setGameText("Deploy on Dining Room. Dark Deal may deploy here if you control three Bespin sites, and is canceled if opponent controls this site. While Dark Deal on table and you occupy Bespin, This Deal Is Getting Worse All The Time flips and may not flip back. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId option,
                                                         boolean asReact) {
        return Filters.Dining_Room;
    }

    private boolean locksObjective(SwccgGame game, PhysicalCard self) {
        return GameConditions.canSpot(game, self, Filters.Dark_Deal)
                && GameConditions.occupies(game, self.getOwner(), Filters.Bespin_system);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var back = Filters.and(Filters.your(self), Filters.Pray_I_Dont_Alter_It_Any_Further);
        return List.of(new MayNotBeFlippedModifier(self, (state, querying) -> locksObjective(state.getGame(), self), back));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
                                                                                   PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isTableChanged(game, result)) return null;
        var actions = new ArrayList<RequiredGameTextTriggerAction>();
        var diningRoom = self.getAttachedTo();
        if (diningRoom != null && GameConditions.controls(game, game.getOpponent(self.getOwner()), diningRoom)) {
            var darkDeal = Filters.findFirstActive(game, self, Filters.Dark_Deal);
            if (darkDeal != null) {
                var action = new RequiredGameTextTriggerAction(self, sourceId);
                action.setSingletonTrigger(true);
                action.setText("Cancel Dark Deal");
                action.appendEffect(new CancelCardOnTableEffect(action, darkDeal));
                actions.add(action);
            }
        }
        if (locksObjective(game, self)) {
            var objective = Filters.findFirstActive(game, self, Filters.and(Filters.your(self),
                    Filters.This_Deal_Is_Getting_Worse_All_The_Time));
            if (objective != null && GameConditions.canBeFlipped(game, objective)) {
                var action = new RequiredGameTextTriggerAction(self, sourceId);
                action.setSingletonTrigger(true);
                action.setText("Flip This Deal Is Getting Worse All The Time");
                action.appendEffect(new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (locksObjective(game, self) && !objective.isFlipped()) {
                            action.appendEffect(new FlipCardEffect(action, objective));
                        }
                    }
                });
                actions.add(action);
            }
        }
        return actions;
    }
}
