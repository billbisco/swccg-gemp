package com.gempukku.swccgo.cards.set1107.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ScumAndVillainyMayDeployAttachedModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 7: Shadows Of The Empire
 * Type: Effect
 * Title: Information Exchange (V)
 */
public class Card1107_017 extends AbstractNormalEffect {
    public Card1107_017() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Information_Exchange, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_7, Rarity.V);
        setVirtualSuffix(true);
        setLore("Chisa nyooda ishaley. Kun Jabba neguda len malta. Ikkit ui! Yobbit, yobbit. Nelan tui ke bada.");
        setGameText("Deploy on Imperial City. While your [Reflections II] Objective on table, Scum And Villainy may deploy here and, if a Black Sun agent present, may not be canceled. During your deploy phase, may ▼ a non-weapon card with 'Black Sun' or 'Xizor' in lore.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Imperial_City;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ScumAndVillainyMayDeployAttachedModifier(self, Filters.hasAttached(self), new OnTableCondition(self, Filters.and(Filters.your(self), Icon.REFLECTIONS_II, Filters.Objective))));
        modifiers.add(new MayNotBeCanceledModifier(self, Filters.Scum_And_Villainy, new AndCondition(new OnTableCondition(self, Filters.and(Filters.your(self), Icon.REFLECTIONS_II, Filters.Objective)), new PresentAtCondition(Filters.Black_Sun_agent, Filters.hasAttached(self)))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.OVS__INFORMATION_EXCHANGE__DEPLOY_BLACK_SUN_OR_XIZOR;
        Filter filter = Filters.and(Filters.not(Filters.weapon), Filters.or(Filters.loreContains("Black Sun"), Filters.loreContains("Black Suns"), Filters.loreContains("Xizor"), Filters.loreContains("Xizors")));

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a non-weapon card with 'Black Sun' or 'Xizor' in lore from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, filter, true));
            actions.add(action);
        }
        return actions;
    }
}
