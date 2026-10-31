package com.gempukku.swccgo.cards.set1109.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9: Sanctuary Moon
 * Type: Effect
 * Title: Wipe Them Out, All Of Them
 */
public class Card1109_050 extends AbstractNormalEffect {
    public Card1109_050() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Wipe Them Out, All Of Them", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("Darth Sidious' command was merciless and direct. He left no room for misinterpretation.");
        setGameText("Deploy on a site. Unless a player controls a battleground site, that player's Permanent Weapon weapon destiny draws are -2. During opponent's draw phase, if opponent's non-battleground site (with no related locations) on table, may activate or retrieve 1 Force. (Immune to Alter.)");
        addIcons(Icon.EPISODE_I, Icon.CORUSCANT);
        addImmuneToCardTitle(Title.Alter);
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.site;
    }

    private Filter opponentsIsolatedNonBattlegroundSite(PhysicalCard self, String opponent) {
        return Filters.and(Filters.owner(opponent), Filters.site, Filters.not(Filters.battleground), new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return !Filters.canSpot(gameState.getGame(), self, Filters.relatedLocation(physicalCard));
            }
        });
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyModifier(self, Icon.PERMANENT_WEAPON,
                new NotCondition(new ControlsCondition(playerId, Filters.battleground_site)),
                Filters.owner(playerId), -2));
        modifiers.add(new EachWeaponDestinyModifier(self, Icon.PERMANENT_WEAPON,
                new NotCondition(new ControlsCondition(opponent, Filters.battleground_site)),
                Filters.owner(opponent), -2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        if (GameConditions.isOnceDuringOpponentsPhase(game, self, playerId, gameTextSourceCardId, Phase.DRAW)
                && GameConditions.canSpot(game, self, opponentsIsolatedNonBattlegroundSite(self, opponent))) {
            if (GameConditions.canActivateForce(game, playerId)) {
                final TopLevelGameTextAction activate = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
                activate.setText("Activate 1 Force");
                activate.appendUsage(
                        new OncePerPhaseEffect(activate));
                activate.appendEffect(
                        new ActivateForceEffect(activate, playerId, 1));
                actions.add(activate);
            }

            final TopLevelGameTextAction retrieve = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            retrieve.setText("Retrieve 1 Force");
            retrieve.appendUsage(
                    new OncePerPhaseEffect(retrieve));
            retrieve.appendEffect(
                    new RetrieveForceEffect(retrieve, playerId, 1));
            actions.add(retrieve);
        }
        return actions;
    }
}
