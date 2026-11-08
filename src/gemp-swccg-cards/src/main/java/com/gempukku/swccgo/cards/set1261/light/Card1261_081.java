package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Incom Corporation & Koensayr Manufacturing, final Virtual Block 6.1. */
public class Card1261_081 extends AbstractNormalEffect {
    public Card1261_081() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "Incom Corporation & Koensayr Manufacturing",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setTitleComponents(new TitleComponent("Incom Corporation", Uniqueness.UNIQUE),
                new TitleComponent("Koensayr Manufacturing", Uniqueness.UNIQUE));
        setGameText("Deploy on a Rebel Base war room. Your ability-1 permanent pilots aboard T-47s, X-wings, Y-wings, and Z-95s (except squadrons) are replaced by ability-2 permanent pilots who add 1 to power. During your control phase, may retrieve one Y-wing or Z-95. Suspended if opponent controls this site. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_SITE); addIcons(Icon.SPECIAL_EDITION); addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self,
            PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.Rebel_Base_site, Filters.war_room);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter ships = Filters.and(Filters.your(self), Filters.hasPermanentPilot, Filters.not(Filters.squadron),
                WidenIncomKoensayrScopeModifier.affectedShips(game, self,
                        Filters.or(Filters.T_47, Filters.X_wing, Filters.Y_wing, Filters.Z_95)));
        return Arrays.asList(new ReplaceAbility1PermanentPilotsModifier(self, ships, 2),
                new PowerModifier(self, Filters.and(ships, Filters.isAbility1PermanentPilotReplaced), 1));
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        // Like printed Koensayr, suspension must remain applied while this Effect is inactive.
        return Collections.singletonList(new SuspendsCardModifier(self, self,
                new ControlsCondition(game.getOpponent(self.getOwner()), Filters.sameSite(self))));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.hasLostPile(game, playerId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Retrieve a Y-wing or Z-95"); action.appendUsage(new OncePerPhaseEffect(action));
            action.appendEffect(new RetrieveCardEffect(action, playerId,
                    WidenIncomKoensayrScopeModifier.affectedShips(game, self, Filters.or(Filters.Y_wing, Filters.Z_95))));
            return Collections.singletonList(action);
        }
        return null;
    }

    /**
     * The PC card list text (PC-E1, the latest) has no "Echo Base Operations is canceled." (on the 2010 sheet and the
     * Master List; 2261_81 keeps it).
     */
    protected boolean cancelsEchoBaseOperations() {
        return false;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect,
            PhysicalCard self, int gameTextSourceCardId) {
        if (cancelsEchoBaseOperations() && TriggerConditions.isPlayingCard(game, effect, Filters.Echo_Base_Operations)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult,
            PhysicalCard self, int gameTextSourceCardId) {
        if (cancelsEchoBaseOperations() && TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Echo_Base_Operations)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Echo_Base_Operations, Title.Echo_Base_Operations);
            return Collections.singletonList(action);
        }
        return null;
    }
}
