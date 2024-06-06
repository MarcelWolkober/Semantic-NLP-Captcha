<template>
  <div class="PairComponent">
    <h2>Pair Challenge {{ pairNumber }} </h2>
    <p>Determine the degree of meaning relatedness of the highlighted word in the following sentences: </p>
    <div class="contexts">
      <p v-html="boldContext1"></p>
      <p v-html="boldContext2"></p>
    </div>
    <div class="button-row">
      <button id="1" @click=saveButtonPress(1)>1 - Unrelated</button>
      <button id="2" @click=saveButtonPress(2)>2 - Distantly Related</button>
      <button id="3" @click=saveButtonPress(3)>3 - Closely Related</button>
      <button id="4" @click=saveButtonPress(4)>4 - Identical</button>
    </div>
    <p v-if="pressedButton != null">You chose: {{ pressedButton }}</p>
    <p v-else>Choose a degree of relatedness</p>
  </div>
</template>


<script>

export default {
  name: "PairComponent",
  props: {
    pair: {
      type: Object,
      required: true
    },
    pairNumber: Number
  },
  data() {
    return {
      id: null,
      stringID: null,
      lemma: null,
      context1: null,
      context2: null,
      indexes1: [-1, 0],
      indexes2: [-1, 0],
      label: 0,
      pressedButton: null

    };
  },
  created() {
    try {
      this.id = this.pair.id;
      // this.stringID = this.pair.identifier1 + "|" + this.pair.identifier2;

      this.usage1 = this.pair.usages[0];
      this.usage2 = this.pair.usages[1];
      this.lemma = this.usage1.lemma;
      this.context1 = this.usage1.context;
      this.context2 = this.usage2.context;
      this.indexes1 = [this.usage1.posStartIndex, this.usage1.posEndIndex];
      this.indexes2 = [this.usage2.posStartIndex, this.usage2.posEndIndex];

    } catch (e) {
      console.log(e);
      this.context1 = "Error loading pair";
    }

  },
  methods: {
    saveButtonPress(id) {
      this.pressedButton = id;
      this.$emit("userChoice", { id: this.id, button: id });
    }

  },
  mounted() {
    this.msg = "Mounted pair with id: " + this.id;
    console.log(this.msg);
  },
  computed: {
    boldContext1() {
      if (this.indexes1[0] === -1) {
        return  this.context1;
      }
      let startIndex = this.indexes1[0];
      let endIndex = this.indexes1[1];
      return this.context1.slice(0, startIndex) +
        "<strong>" + this.context1.slice(startIndex, endIndex) + "</strong>" +
        this.context1.slice(endIndex, this.context1.length);
    },
    boldContext2() {
      if (this.indexes2[0] === -1) {
        return  this.context1;
      }
      let startIndex = this.indexes2[0];
      let endIndex = this.indexes2[1];
      return this.context2.slice(0, startIndex) +
        "<strong>" + this.context2.slice(startIndex, endIndex) + "</strong>" +
        this.context2.slice(endIndex, this.context2.length);
    }
  }

};
</script>


<style scoped>
.button-row {
  display: flex;
  justify-content: center;
  gap: 10px;
}
.contexts {
  gap: 5px;

}

</style>