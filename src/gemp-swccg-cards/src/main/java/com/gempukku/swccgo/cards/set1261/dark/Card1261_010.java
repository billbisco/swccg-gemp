package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.List;

/** Legacy Block 6.1 Darth Vader, Betrayer Of The Jedi. */
public class Card1261_010 extends AbstractImperial {
    private static final Filter JEDI = Filters.or(Filters.and(Filters.ObiWan, Filters.icon(Icon.REFLECTIONS_III)),
            Filters.Jedi_Council_member);

    public Card1261_010() {
        super(Side.DARK, 1, 6, 6, 6, 8, "Darth Vader, Betrayer Of The Jedi", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("'I will deal with them myself.'");
        setGameText("[Pilot] 3. The Circle Is Now Complete is canceled. While present with [Reflections III] Obi-Wan or a Jedi Council Member, may add one battle destiny, lightsabers here may not be 'swung' twice, and their immunity to attrition is canceled. Immune to attrition < 5.");
        addPersona(Persona.VADER);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter lightsabersHere = Filters.and(Filters.or(Filters.lightsaber, Filters.hasPermanentWeapon(Filters.lightsaber)),
                Filters.here(self));
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 3),
                new MayFireOnlyOncePerBattleModifier(self, lightsabersHere, new PresentWithCondition(self, JEDI)),
                new CancelImmunityToAttritionModifier(self, Filters.and(JEDI, Filters.presentWith(self))),
                new ImmuneToAttritionLessThanModifier(self, 5));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                     PhysicalCard self, int sourceId) {
        var battle = game.getGameState().getBattleState();
        var id = GameTextActionId.OTHER_CARD_ACTION_1;
        if (battle == null || battle.isCanceled() || battle.isReachedPowerSegment()
                || !GameConditions.isDuringBattleWithParticipant(game, self)
                || !GameConditions.canSpot(game, self, Filters.presentWith(self, JEDI))
                || !GameConditions.canAddBattleDestinyDraws(game, self)
                || !GameConditions.isOncePerBattle(game, self, playerId, sourceId, id))
            return null;

        var action = new TopLevelGameTextAction(self, playerId, sourceId, id);
        action.setText("Add one battle destiny");
        action.appendUsage(new OncePerBattleEffect(action));
        action.appendEffect(new AddBattleDestinyEffect(action, 1, playerId));
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect,
                                                                                    PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isPlayingCard(game, effect, Filters.The_Circle_Is_Now_Complete)
                || !GameConditions.canCancelCardBeingPlayed(game, self, effect))
            return null;

        var action = new RequiredGameTextTriggerAction(self, sourceId);
        CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
        return List.of(action);
    }
}
