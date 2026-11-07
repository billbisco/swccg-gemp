package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.StackCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitMayNotBeReducedBelowModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeUndercoverModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotRelocateModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Emperor's Sinister Plans, Virtual Block 7.1. */
public class Card1271_037 extends AbstractUtinniEffect {
    private static final TargetId WAR_ROOM = TargetId.UTINNI_EFFECT_TARGET_1;

    public Card1271_037() {
        super(Side.LIGHT, 6, PlayCardZoneOption.ATTACHED, "Emperor's Sinister Plans", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setLore("Hologram technology allows efficient communication of complex intelligence during war room briefings.");
        setGameText("Deploy on your non-droid spy at Back Door. Target your war room. Until completed, spy may not relocate or be Undercover. When Utinni Effect reaches target, stack there: opponent's battle destiny draws are -1 and your spies may not have their forfeit reduced below 3.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self,
            PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.character, Filters.spy, Filters.not(Filters.droid),
            Filters.not(Filters.undercover_spy), Filters.at(Filters.Back_Door));
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self,
            PhysicalCard deployTarget, TargetId targetId) {
        return warRoom(self);
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId) {
        return warRoom(self);
    }

    private Filter warRoom(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.site, Filters.war_room);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
            PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isTableChanged(game, result)) return null;
        PhysicalCard room = self.getTargetedCard(game.getGameState(), WAR_ROOM);
        if (room == null || !Filters.and(Filters.onTable, warRoom(self)).accepts(game, room)
                || !Filters.at(room).accepts(game, self)) return null;

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Stack completed Emperor's Sinister Plans on the war room");
        action.appendEffect(new StackCardFromTableEffect(action, self, room));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        // Until completed (while still deployed on the spy).
        Filter spy = Filters.hasAttached(self);
        return List.of(new MayNotRelocateModifier(self, spy), new MayNotBeUndercoverModifier(self, spy));
    }

    @Override
    protected List<Modifier> getGameTextWhileStackedModifiers(SwccgGame game, PhysicalCard self) {
        if (self.getStackedOn() == null || !warRoom(self).accepts(game, self.getStackedOn())) return List.of();
        return List.of(new EachBattleDestinyModifier(self, -1, game.getOpponent(self.getOwner())),
            new ForfeitMayNotBeReducedBelowModifier(self, Filters.and(Filters.your(self), Filters.spy), 3));
    }
}
