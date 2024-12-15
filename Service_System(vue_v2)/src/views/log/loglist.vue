<template>
    <Nav></Nav>

    <div style="margin: 20px;">
        <el-form :inline="true" :model="addmodel" class="demo-form-inline">
            <el-form-item label="Add Log">

                <el-input v-model="addmodel.tags" placeholder="input the tags"></el-input>
            </el-form-item>
            <el-form-item label=" select Image ">
                <input type="file" @change="handleFileChange" />
                <el-button style="margin-left: 10px;" size="small" type="success"
                    @click="uploadimage">submit</el-button>
            </el-form-item>
        </el-form>
    </div>
    <div style="margin: 20px;">

        <el-form :inline="true" :model="formInline" class="demo-form-inline">
            <el-form-item label="Photo Date">
                <el-date-picker v-model="formInline.time_range" type="datetimerange" range-separator="To"
                    value-format="YYYY-MM-DD hh:mm:ss" format="YYYY-MM-DD hh:mm:ss" start-placeholder="Start date"
                    end-placeholder="End date" clearable />
            </el-form-item>
            <el-form-item label="Photo Tag">
                <el-select v-model="formInline.tag" placeholder="select tag" clearable>
                    <el-option v-for="item in tagData" :key="item" :label="item" :value="item" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="getData">Query</el-button>
            </el-form-item>
        </el-form>
        <el-table :data="tableData" style="width: 100%;max-height: 1000px;">
            <el-table-column prop="created_date" label="Date" width="180" />
            <el-table-column prop="tags" label="Tag" width="180">
                <template #default="scope">
                    <el-tag v-for="tag in scope.row.tagsarr" :key="tag" closable :disable-transitions="false"
                        @close="handleClose(scope.row, tag)">
                        {{ tag }}
                    </el-tag>
                    <el-input v-if="scope.row.showinput" ref="InputRef" v-model="inputValue" class="w-20" size="small"
                        @keyup.enter="handleInputConfirm(scope.row)" @blur="handleInputConfirm(scope.row)" />
                    <el-button v-else class="button-new-tag" size="small" @click="scope.row.showinput = true">
                        + New Tag
                    </el-button>
                </template>
            </el-table-column>
            <el-table-column prop="image" label="Image">
                <template #default="scope">

                    <el-image :src="`http://127.0.0.1:8000/media/` + scope.row.image" style="width: 300px; z-index: 1;"
                        @click="openNewTab(`http://127.0.0.1:8000/media/` + scope.row.image)">
                    </el-image>
                </template>

            </el-table-column>
        </el-table>

        <el-pagination background layout="prev, pager, next" :total="total" page-sizes="20"
            @current-change="pageChange" />

    </div>

</template>
<script setup>
import Nav from '../nav/Nav.vue'

</script>
<script>

import axios from "axios";

export default {
    data() {
        return {
            addmodel: {
                tags: "",
                post: "2",
                create_time: Date.now(),
                image: undefined
            },
            tableData: [
                {
                    date: '2016-05-03',
                    name: 'Tom',
                    address: 'No. 189, Grove St, Los Angeles',
                },
                {
                    date: '2016-05-02',
                    name: 'Tom',
                    address: 'No. 189, Grove St, Los Angeles',
                }
            ],
            tagData: [

            ],
            inputValue: "",
            formInline: {
                tag: "",
                time_range: "",
                pager: 1
            },
            totalpage: 1,
            total: 1,
            inputVisible: false,
            imgsrc: undefined,
            username: "123123",
            intervalId: 1,
            password: "",
            errorMessage: "",
        };
    },
    mounted() {
        this.getData();
        this.getAllTagData();
        this.addmodel.create_time = Date.now
    },
    methods: {
        openNewTab(url) {
            // 使用 window.open 打开新的标签页
            window.open(url, '_blank');
        },
        handleFileChange(event) {
            this.addmodel.image = event.target.files[0]; // Store the selected file
        },
        async getData() {
            try {
                console.log(this.formInline)
                this.formInline.starttime = this.formInline.time_range[0]
                this.formInline.endtime = this.formInline.time_range[1]
                const response = await axios.get("http://127.0.0.1:8000/api/queryphotos", {
                    params: this.formInline,
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem("jwtToken")}`

                    }
                });
                console.log(response)
                response.data.items.forEach(element => {
                    element.showinput = false
                    element.imagelist = ["http://127.0.0.1:8000/media/" + element.image]
                    if (element.tags != undefined) {
                        element.tagsarr = element.tags.split('_').filter(item => item.trim() !== "")
                    }
                });
                this.tableData = response.data.items;
                this.totalpage = response.data.total_pages;
                this.total = response.data.total_items;
            } catch (error) {
                this.errorMessage = "Invalid username or password";
            }
        },
        async uploadimage() {
            try {
                // this.$refs.upload.submit();
                console.log(this.addmodel.image)
                if (this.addmodel.image == undefined) {
                    alert("please select a image")
                    return
                }
                const formData = new FormData();
                formData.append('image', this.addmodel.image); // Append the image file
                formData.append('tags', this.addmodel.tags); // Append the title
                formData.append('create_time', new Date());
                formData.append('post', 2);

                try {
                    const response = await axios.post("http://127.0.0.1:8000/api/addPhotos", formData, {
                        headers: {
                            'Authorization': `Bearer ${localStorage.getItem("jwtToken")}`
                        },
                    });
                    console.log('Upload successful:', response.data);
                    this.getData()
                } catch (error) {
                    console.error('Error uploading image:', error);
                }

            } catch (error) {
                this.errorMessage = "Invalid username or password";
            }
        },
        pageChange(pageindex) {
            this.formInline.pager = pageindex
            this.getData()
        },
        async handleInputConfirm(row) {
            row.showinput = false;
            row.tagsarr.push(this.inputValue)
            row.tags += "_" + this.inputValue
            const response = await axios.get("http://127.0.0.1:8000/api/settages/" + row.id + "/" + row.tags + "/", {
                params: this.formInline,
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem("jwtToken")}`
                }
            });

        },
        async handleClose(row, tag) {
            var newarr = []
            row.tags = "_";
            row.tagsarr.forEach(element => {
                if (element != tag) {
                    newarr.push(element)
                    row.tags += element + "_"
                }
            });
            row.tagsarr = newarr
            const response = await axios.get("http://127.0.0.1:8000/api/settages/" + row.id + "/" + row.tags + "/", {
                params: this.formInline,
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem("jwtToken")}`
                }
            });
        },
        splicetags(tags) {
            if (tags != undefined) {
                var arr = tags.split('_')
                if (arr != undefined) {
                    arr = arr.filter(item => item.trim() !== "")
                    return arr
                }
            }
            return []

        },
        async getAllTagData() {
            try {
                console.log(this.formInline)
                const response = await axios.get("http://127.0.0.1:8000/api/getalltags", {
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem("jwtToken")}`

                    }
                });
                console.log(response)
                this.tagData = response.data

            } catch (error) {
                this.errorMessage = "Invalid username or password";
            }
        },

    },
};
</script>

<style>
.demo-form-inline .el-input {
    --el-input-width: 220px;
}

.demo-form-inline .el-select {
    --el-select-width: 220px;
}
</style>