<template>
  <div class="UsageComponent" v-bind:class="{ 'border-style': border }">
    <p>
      <span v-if="originalIndex">{{ originalIndex }}. </span>
      <span v-html="boldContext"></span>
    </p>  </div>
</template>

<script>
export default {
  name: "UsageComponent",
  props: {
    ID: Number,
    lemma: String,
    context: String,
    indexes: Array,
    border: Boolean,
    originalIndex: Number
  },
  computed: {
    boldContext() {
      if (this.indexes[0] === -1) {
        return "Context 1:" + this.context;
      }
      let startIndex = this.indexes[0];
      let endIndex = this.indexes[1];
      return "Context: " + this.context.slice(0, startIndex) +
        "<strong>" + this.context.slice(startIndex, endIndex) + "</strong>" +
        this.context.slice(endIndex, this.context.length);
    }

  }


};
</script>

<style scoped>
.border-style {
  border: 1px solid black;
  padding: 10px;
  margin: 10px;
}
</style>