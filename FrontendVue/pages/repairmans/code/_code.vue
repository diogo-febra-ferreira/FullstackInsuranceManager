<template>
  <div>
    <nav class="navbar navbar-expand-md navbar-dark bg-dark">
      <div class="container">
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDropdown"
          aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNavDropdown">
          <ul class="navbar-nav mx-auto">
            <li class="nav-item">
              <nuxt-link :to="`/repairmans/${this.$auth.user.username}`" class="nav-link mx-2">Profile</nuxt-link>
            </li>
            <li class="nav-item">
              <a class="nav-link mx-2 btn btn-danger" @click.prevent="signOut">Sign Out</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <b-container>
      <b-col md="12" class="d-flex flex-column align-items-center">
        <h4 style="padding:20px">Occurrence Details: </h4>
        <div style="margin-top: 30px">
          <p><b>Description: </b>{{ occurrence.description }}</p>
          <p><b>Client name: </b>{{ client.name }}</p>
          <p><b>Client email: </b>{{ client.email }}</p>
          <p><b>Status: </b>{{ occurrence.status }}</p>
          <p><b>Insurance: </b>{{ occurrence.insurance }}</p>
          <p><b> Document Associated: </b>{{ document.filename }}</p>
          <div class="mx-auto" style="margin-top:50px;">
            <b-btn class="btn btn-primary" @click.prevent="download(document)" tar-get="_blank">Download</b-btn>
          </div>
        </div>
      </b-col>
    </b-container>


    <div v-if="occurrence.status === 'Repairing'">

    <div class="mx-auto" style="width: 600px; margin-top: 50px;">
      <div class="mt-3" style="text-align: center;">
        <a><b>Complete Occurrence</b></a>
      </div>
      <div>
        <form @submit.prevent="upload">
          <b-form-file v-model="file" :state="hasFile" placeholder="Choose a file or drop it here..."
            drop-placeholder="Drop file here..." />

          <div class="mt-3">
            Selected file: {{ file? file.name : '' }}
          </div>
          <div class="mt-3" style="text-align: center; padding: 50px;">
            <b-button type="submit" :disabled="!hasFile" @click="changeVisibility()">Upload</b-button>
            <b-button class="btn-success" v-if="isVisible" @click.prevent="complete()">Complete</b-button>
          </div>
        </form>
      </div>
    </div>
    </div>
    <div v-else>
      <div class="mx-auto" style="width: 600px; margin-top: 50px;">
        <div class="mt-3" style="text-align: center;">
          <a><b>This occurence cannot be completed anymore, it's not 'Repairing'</b></a>
          </div>
        </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  data() {
    return {
      visible: false,
      description: null,
      type: null,
      file: null,
      client: {},
      occurrence: {},
      options: [
        { value: null, text: 'Please select an option', disabled: true }
      ],
      document: {}
    }
  },
  created() {
    //get this occurrence
    this.$axios.$get('/api/occurrences/' + this.code)
      .then((occurrence) => {
        this.occurrence = occurrence || {}
        if (this.occurrence)
          this.client = occurrence.client
        this.document = occurrence.document
        //get repairmen related to this occurrence
      })
  },

  computed: {
    code() {
      return this.$route.params.code
    },
    hasFile() {
      return this.file != null
    },
    isVisible() {
      return this.visible
    },
    formData() {
      let formData = new FormData()
      formData.append('username', this.$auth.user.username)
      if (this.file) {
        formData.append('file', this.file)
      }
      if (this.description) {
        formData.append('description', this.description)
      }
      return formData
    }
  },

  methods: {
    signOut() {
      this.$auth.logout()
      this.$router.push('/')
    },
    changeVisibility() {
      this.visible = true
    },
    download(doc) {
      this.$axios.$get('/api/documents/download/' + doc.id, { responseType: 'arraybuffer' })
        .then(file => {
          const url = window.URL.createObjectURL(new Blob([file]))
          const link = document.createElement('a')
          link.href = url
          link.setAttribute('download', doc.filename)
          document.body.appendChild(link)
          link.click()
        })
    },
    upload() {
      if (!this.hasFile) {
        this.$toast.error('Please select a file to upload.').goAway(3000)
        return
      }

      this.$axios.$post('/api/documents', this.formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
        .then((document) => {
          this.$axios.$put(`api/documents/${this.code}/${document.at(0).id}/edit/`)
            .then(() => {
              this.$toast.success("Success downloading file").goAway(1000);//adicionar aqui o router push
            })
            .catch(() => {
              this.$toast.error('Error downloading file').goAway(1000);
            })
        })
        .catch(() => {
          this.$toast
            .error("Sorry, error uploading document. Couldn't register occurrence")
            .goAway(3000)
        })
    },
    complete() {
      this.$axios.$post(`api/repairmen/update/occurrence/${this.code}`).then(() => {
        this.$toast.success("Success updating occurrence").goAway(1000);
        this.$router.go(0)
      })
    }
  }
}
</script>
