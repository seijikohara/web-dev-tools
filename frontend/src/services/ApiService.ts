import axios from "axios";

const api = axios.create({
  baseURL: `${process.env.VUE_APP_API_BASE_URL}api`,
});
api.interceptors.response.use(
  (response) => response,
  (error) => Promise.reject(error)
);

export type IpInfo = {
  ipAddress: string;
  hostName: string;
};

export type HttpHeader = {
  name: string;
  value: string;
};

export type HttpHeaders = {
  headers: HttpHeader[];
};

export type HtmlEntities = {
  content: Content[];
  pageable?: Pageable;
  last: boolean;
  totalPages: number;
  totalElements: number;
  numberOfElements: number;
  sort?: Sort;
  first: boolean;
  number: number;
  size: number;
  empty: boolean;
};

export type Content = {
  name: string;
  code: number;
  code2: number | null;
  standard: string | null;
  dtd: string | null;
  description: string | null;
  entityReference: string;
};

export type Pageable = {
  sort: Sort;
  offset: number;
  pageNumber: number;
  pageSize: number;
  paged: boolean;
  unpaged: boolean;
};

export type Sort = {
  sorted: boolean;
  unsorted: boolean;
  empty: boolean;
};

export default {
  async getIpAddress(): Promise<IpInfo> {
    const response = await api.get("ip");
    return response.data as IpInfo;
  },
  async getHttpHeader(): Promise<HttpHeaders> {
    const response = await api.get("http-headers");
    return response.data as HttpHeaders;
  },
  async getGeo(ipAddress: string): Promise<unknown> {
    const response = await api.get(`geo/${ipAddress}`);
    return response.data;
  },
  async getRdap(ipAddress: string): Promise<unknown> {
    const response = await api.get(`rdap/${ipAddress}`);
    return response.data;
  },
  async getHtmlEntities(
    name: string,
    page: number,
    size: number
  ): Promise<HtmlEntities> {
    const response = await api.get(`html-entities`, {
      params: {
        name,
        page,
        size,
      },
    });
    return response.data;
  },
};
