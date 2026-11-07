package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RecordUtinniEffectCompletedEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.List;

/** The First Transport Is Away! (V), Legacy Block 6.1, final Light master page 30. */
public class Card1261_088 extends AbstractUtinniEffect {
    private static final Filter VIRTUAL_FIRST_MARKER = Filters.and(Filters.First_Marker,
            new Filter() {
                @Override
                public boolean accepts(GameState state, ModifiersQuerying querying, PhysicalCard card) {
                    return card.getBlueprint().hasVirtualSuffix();
                }
            });
    private static final Filter UNSHIELDED_MARKER = Filters.and(Filters.marker_site, Filters.not(Filters.underHothEnergyShield()));

    public Card1261_088() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.The_First_Transport_Is_Away, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'When you've gotten past the energy shield, proceed directly to the rendezvous point. Understood? Good luck!'");
        setGameText("Deploy on Rendezvous Point if [Virtual] 1st Marker on table. Target a transport on Hoth. When reached by target, relocate Utinni Effect to Hoth system. Your Force drains at unshielded marker sites are +1 and cannot be canceled. Immune to Surprise. (Immune to Alter.)");
        addIcons(Icon.HOTH);
        addImmuneToCardTitle(Title.Surprise);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, VIRTUAL_FIRST_MARKER) ? Filters.Rendezvous_Point : Filters.none;
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.and(Filters.transport, Filters.at(Filters.Hoth_location));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(SwccgGame game, PhysicalCard self) {
        return Filters.or(Filters.Rendezvous_Point, Filters.Hoth_system);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var atHoth = new AttachedCondition(self, Filters.Hoth_system);
        return List.of(new ForceDrainModifier(self, UNSHIELDED_MARKER, atHoth, 1, self.getOwner()),
                new ForceDrainsMayNotBeCanceledModifier(self, UNSHIELDED_MARKER, atHoth, self.getOwner()));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isUtinniEffectReached(game, self) || !TriggerConditions.isTableChanged(game, result)) {
            return null;
        }
        PhysicalCard target = self.getTargetedCard(game.getGameState(), TargetId.UTINNI_EFFECT_TARGET_1);
        if (target == null || !GameConditions.isAtLocation(game, self, Filters.sameLocation(target))) {
            return null;
        }
        PhysicalCard hoth = Filters.findFirstFromTopLocationsOnTable(game, Filters.Hoth_system);
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setSingletonTrigger(true);
        action.setText("Relocate The First Transport Is Away! to Hoth");
        action.appendUsage(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                self.setUtinniEffectStatus(UtinniEffectStatus.COMPLETED);
            }
        });
        action.appendEffect(new RecordUtinniEffectCompletedEffect(action, self));
        if (hoth != null) {
            action.appendEffect(new AttachCardFromTableEffect(action, self, hoth));
        }
        return List.of(action);
    }
}
