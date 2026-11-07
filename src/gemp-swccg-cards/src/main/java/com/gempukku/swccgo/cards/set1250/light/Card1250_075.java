package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Legacy Block 5 Kashyyyk: Sacred Forest, assigned printing 75. */
public class Card1250_075 extends AbstractSite {
    public Card1250_075() {
        super(Side.LIGHT, "Kashyyyk: Sacred Forest", Title.Kashyyyk, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setLocationLightSideGameText("At start of your turn, may draw a card from Reserve Deck for each Kashyyyk site where all your ability is provided by Wookiees. Once per turn, may deploy Yarna d'al' Gargan from Reserve Deck; reshuffle.");
        setLocationDarkSideGameText("Unless Mosep on table, Force lost from your Force drains here must come from opponent's hand, if possible. All Nudjs are lost.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.ENDOR, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeyword(Keyword.FOREST);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.isStartOfYourTurn(game, result, playerId) || !GameConditions.hasReserveDeck(game, playerId)) return null;

        int count = 0;
        for (PhysicalCard site : Filters.filterTopLocationsOnTable(game, Filters.Kashyyyk_site)) {
            if (GameConditions.isAllAbilityAtLocationProvidedBy(game, self, playerId, site, Filters.Wookiee)) count++;
        }
        if (count == 0) return null;

        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
        action.setText("Draw from Reserve Deck for Wookiee sites");
        action.appendEffect(new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, count));
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId id = GameTextActionId.LEGACY__SACRED_FOREST__DEPLOY_YARNA;
        if (!GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, id)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id, Title.Yarna_dal_Gargan)) return null;

        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, id);
        action.setText("Deploy Yarna d'al' Gargan from Reserve Deck");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Yarna_dal_Gargan), true));
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.isTableChanged(game, result)) return null;
        var nudjs = Filters.filterAllOnTable(game, Filters.and(Filters.in_play, Filters.title("Nudj")));
        if (nudjs.isEmpty()) return null;

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setSingletonTrigger(true);
        action.setText("All Nudjs are lost");
        action.appendEffect(new LoseCardsFromTableSimultaneouslyEffect(action, nudjs, true, false));
        return List.of(action);
    }
}
