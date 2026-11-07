package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import java.util.List;

/** Legacy Block 6.2: Dathomir Witch. */
public class Card1262_002 extends AbstractRepublic {
    public Card1262_002() {
        super(Side.DARK, 2, 3, 2, 4, 5, "Dathomir Witch", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setLore("Female.");
        setGameText("Landspeed = 2. If present with exactly one ferocious creature (even if excluded; except Bubo or Sarlacc), your power here is +X (where X = double creature's defense value). Once per turn, may deploy Rancor Mount here from Reserve Deck; reshuffle. Cannot be attacked.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR, Icon.LEGACY_BLOCK_6);
        addKeyword(Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter eligibleCreature = Filters.and(Filters.creature, Filters.not(Filters.or(Filters.parasite, Filters.Bubo, Filters.Sarlacc)),
                Filters.presentWith(self), new Filter() {
                    @Override
                    public boolean accepts(GameState state, ModifiersQuerying querying, PhysicalCard card) {
                        // Legacy AR 2.0: selective non-parasites with landspeed > 1 are packhunters.
                        return !Filters.icon(Icon.SELECTIVE_CREATURE).accepts(state, querying, card)
                                || querying.getLandspeed(state, card) <= 1;
                    }
                });
        return List.of(new DefinedByGameTextLandspeedModifier(self, 2), new MayNotBeAttackedModifier(self),
                new TotalPowerModifier(self, Filters.here(self), new BaseEvaluator() {
                    @Override
                    public float evaluateExpression(GameState state, ModifiersQuerying querying, PhysicalCard location) {
                        var creatures = Filters.filterActive(state.getGame(), self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, eligibleCreature);
                        return creatures.size() == 1 ? 2 * querying.getDefenseValue(state, creatures.iterator().next()) : 0;
                    }
                }, self.getOwner()));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        GameTextActionId id = GameTextActionId.LEGACY__DATHOMIR_WITCH__DEPLOY_RANCOR_MOUNT;
        if (GameConditions.isOncePerTurn(game, self, playerId, sourceId, id)
                && Filters.at(Filters.site).accepts(game, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) {
            var action = new TopLevelGameTextAction(self, playerId, sourceId, id);
            action.setText("Deploy Rancor Mount from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.title("Rancor Mount"), Filters.here(self), true));
            return List.of(action);
        }
        return null;
    }
}
