<template>
  <div class="ListChallengeComponent">
    <h2>List-Challenge</h2>
    <p> Order the following list by how similar the meaning of the word <strong>{{ lemma }}</strong> is compared to the
      reference Usage. With the most similar being on top of the list </p>
    <p>Reference Usage:</p>
    <div class="referenceUsage">
      <UsageComponent :i-d="referenceUsage.id" :lemma="referenceUsage.lemma" :context="referenceUsage.context"
                      :indexes="[referenceUsage.posStartIndex, referenceUsage.posEndIndex]" />
    </div>
    <p>Usage list to order:</p>
    <draggable
      v-model="usages"
      @start="drag=true"
      @end="drag=false"
      @change="getCurrentOrder"
      group="usageList">
      <UsageComponent
        v-for="(usage, index) in usages"
        :key="usage.id"
        :index="index"
        :id="usage.id"
        :lemma="usage.lemma"
        :context="usage.context"
        :indexes="[usage.posStartIndex, usage.posEndIndex]"
        :border="true"
        :originalIndex="getOriginalIndex(usage)"
      />
    </draggable>
    <button @click="submitOrder">Submit Order</button>
  </div>
</template>

<script>
import UsageComponent from "@/components/challenges/UsageComponent.vue";
import { VueDraggableNext } from "vue-draggable-next";

export default {
  name: "ListChallengeComponent",
  components: {
    UsageComponent,
    draggable: VueDraggableNext
  },
  props: {
    _id: Number,
    _lemma: String,
    _referenceUsage: Object,
    _usages: Array
  },
  data() {
    return {
      id: this._id,
      lemma: this._lemma,
      referenceUsage: this._referenceUsage,
      usages: this._usages,
      drag: false,
      usagesWithOriginalIndex: this._usages.map((usage, index) => ({ usage, originalIndex: index + 1 })),
      currentOrder: [],
    };
  },
  computed: {},
  methods: {
    getOriginalIndex(usage) {
      const usageWithOriginalIndex = this.usagesWithOriginalIndex.find(u => u.usage.id === usage.id);
      return usageWithOriginalIndex ? usageWithOriginalIndex.originalIndex : null;
    },
    getCurrentOrder() {
      this.currentOrder = this.usages.map(usage => usage.id);
      console.log("current order:", this.currentOrder);
    },
    submitOrder() {
      this.$emit('submitListChallenge', { challengeId: this.id, order: this.currentOrder });
    }
  },
  created() {
    console.log("ListChallengeComponent created");
    console.log(this._usages);
  }
};
</script>

<style scoped>
.ListChallengeComponent {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.usageList {
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>